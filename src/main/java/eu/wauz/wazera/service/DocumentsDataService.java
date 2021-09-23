package eu.wauz.wazera.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.WazeraTool;
import eu.wauz.wazera.model.data.auth.UserData;
import eu.wauz.wazera.model.data.docs.DocType;
import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.docs.FolderData;
import eu.wauz.wazera.model.entity.docs.Document;
import eu.wauz.wazera.model.entity.docs.DocumentTag;
import eu.wauz.wazera.model.entity.docs.Folder;
import eu.wauz.wazera.model.entity.docs.FolderUserData;
import eu.wauz.wazera.model.repository.docs.DocumentRepository;
import eu.wauz.wazera.model.repository.docs.DocumentTagRepository;
import eu.wauz.wazera.model.repository.docs.FolderRepository;
import eu.wauz.wazera.model.repository.docs.FolderUserDataRepository;
import eu.wauz.wazera.model.repository.docs.jpa.DocumentJpaRepository;
import eu.wauz.wazera.model.repository.docs.jpa.FolderJpaRepository;

@Service
@Scope("singleton")
public class DocumentsDataService {
	
	@Autowired
	private FoldersDataService foldersService;
	
	@Autowired
	private TasksDataService tasksService;
	
	@Autowired
	private AuthDataService authService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentJpaRepository documentJpaRepository;

    @Autowired
    private DocumentTagRepository documentTagRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderJpaRepository folderJpaRepository;

    @Autowired
    private FolderUserDataRepository folderUserDataRepository;
    
	private WazeraTool wazeraTool;

    @PostConstruct
    public void init() {
    	wazeraTool = new WazeraTool();
    }

    public FolderData getDocuments(int treeId, Integer docId, Integer folderId, List<String> searchTokens) throws Exception {
    	Integer userId = authService.getLoggedInUserId();
        FolderData rootNode = null;
        Folder rootFolder = folderRepository.findById(treeId).orElse(null);
        if(docId != null) {
        	expandDocumentParentFolders(docId, userId);
        }
        else if(folderId != null) {
        	expandFolderParentFolders(folderId, userId);
        }
        rootNode = foldersService.readFolderData(rootFolder, userId);

        Set<String> addedFiles = new HashSet<>();
        if(!searchTokens.isEmpty()) {
        	Set<Folder> matchingFolders = new HashSet<>(folderJpaRepository.findByTags(searchTokens));
	        Set<Document> matchingDocuments = new HashSet<>(documentJpaRepository.findByTags(searchTokens));
	        Map<Integer, FolderData> folderDataMap = new HashMap<>();
	        Map<Integer, Folder> folderMap = new HashMap<>();
	        matchingFolders.remove(rootFolder);
	        folderMap.put(rootFolder.getId(), rootFolder);
	        folderDataMap.put(rootNode.getId(), rootNode);
	        Queue<Folder> queue = new ArrayDeque<>(matchingFolders);
	        
	        while(!queue.isEmpty()) {
        		Folder folder = queue.poll();
        		addFolderData(folderDataMap, folderMap, folder.getId(), userId);
        		matchingDocuments.addAll(documentRepository.findByFolderIdOrderBySortOrder(folder.getId()));
	        	queue.addAll(folderRepository.findByFolderIdOrderBySortOrder(folder.getId()));
        	}
	        for(Document document : matchingDocuments) {
	        	FolderData documentFolderData = addFolderData(folderDataMap, folderMap, document.getFolderId(), userId);
	        	if(documentFolderData != null && documentFolderData.getDocuments() != null) {
	        		documentFolderData.getDocuments().add(readDocumentData(document));
	        	}
			}
	        queue.addAll(folderMap.values());
	        
	        while(!queue.isEmpty()) {
	        	Folder folder = queue.poll();
	        	Folder parent = null;
	        	if(folder.getFolderId() != null) {
	        		parent = folderRepository.findById(folder.getFolderId()).orElse(null);
	        	}
	        	if(parent == null) {
	        		continue;
	        	}
	        	
        		FolderData parentFolderData = addFolderData(folderDataMap, folderMap, parent.getId(), userId);
        		FolderData folderData = addFolderData(folderDataMap, folderMap, folder.getId(), userId);
        		folderData.setExpanded(true);
        		if(!parentFolderData.getFolders().contains(folderData)) {
        			parentFolderData.getFolders().add(folderData);
        			queue.offer(parent);
        		}
	        }
	        
	        Queue<FolderData> sortQueue = new ArrayDeque<>(Collections.singleton(rootNode));
	        while(!sortQueue.isEmpty()) {
	        	FolderData folderData = sortQueue.poll();
	        	folderData.getDocuments().sort((d1, d2) -> Integer.compare(d1.getSortOrder(), d2.getSortOrder()));
	        	folderData.getFolders().sort((f1, f2) -> Integer.compare(f1.getSortOrder(), f2.getSortOrder()));
	        	sortQueue.addAll(folderData.getFolders());
			}
        }
        else {
        	addFolders(rootFolder, rootNode, addedFiles, userId);
        	addDocuments(rootFolder, rootNode, addedFiles, userId);
        }
        return rootNode;
    }

    private void expandDocumentParentFolders(Integer docId, int userId) {
    	if(docId == null) {
    		return;
    	}
    	Document document = documentRepository.findById(docId).orElse(null);
    	if(document != null) {
    		expandFolderParentFolders(document.getFolderId(), userId);
    	}
    }
    
    private void expandFolderParentFolders(Integer folderId, int userId) {
    	while(folderId != null) {
    		Folder parentFolder = folderRepository.findById(folderId).orElse(null);
    		if(parentFolder != null) {
    			FolderData parentFolderData = foldersService.readFolderData(parentFolder, userId);
    			parentFolderData.setExpanded(true);
    			saveFolderUserData(parentFolderData);
    			folderId = parentFolder.getFolderId();
    		}
    		else {
    			folderId = null;
    		}
    	}
    }

    private void saveFolderUserData(FolderData folderData) {
    	Integer userId = authService.getLoggedInUserId();
		FolderUserData folderUserDataFromRepo = folderUserDataRepository.findByFolderIdAndUserId(folderData.getId(), userId);
		FolderUserData folderUserData = folderUserDataFromRepo != null ? folderUserDataFromRepo : new FolderUserData();
		folderUserData.setUserId(userId);
		folderUserData.setFolderId(folderData.getId());
		folderUserData.setExpanded(folderData.isExpanded() != null ? folderData.isExpanded() : false);
		folderUserDataRepository.save(folderUserData);
	}

	private FolderData addFolderData(Map<Integer, FolderData> folderDataMap, Map<Integer, Folder> folderMap, Integer folderId, Integer userId) {
    	FolderData folderData = folderDataMap.get(folderId);
    	if(folderData == null) {
    		Folder folder = folderRepository.findById(folderId).orElse(null);
    		if(folder != null) {
    			folderMap.put(folderId, folder);
    			folderData = foldersService.readFolderData(folder, userId);
    			folderDataMap.put(folderId, folderData);
    		}
    	}
    	return folderData;
    }

    private void addFolders(Folder folder, FolderData node, Set<String> addedFiles, Integer userId) throws Exception {
    	if(folder.getFolderId() != null) {
			FolderUserData folderUserData = folderUserDataRepository.findByFolderIdAndUserId(folder.getId(), userId);
			if(folderUserData == null || !folderUserData.getExpanded()) {
				return;
			}
		}
    	
    	List<Folder> childFolders = folderRepository.findByFolderIdOrderBySortOrder(folder.getId());
    	for (Folder childFolder : childFolders) {
    		FolderUserData childFolderUserData = folderUserDataRepository.findByFolderIdAndUserId(childFolder.getId(), userId);

            FolderData childNode = new FolderData();
            childNode.setId(childFolder.getId());
            childNode.setName(childFolder.getName());
            childNode.setExpanded(childFolderUserData != null ? childFolderUserData.getExpanded() : false);

            node.getFolders().add(childNode);

            if(childFolderUserData == null || !childFolderUserData.getExpanded()) {
            	int contentAmount = 0;
            	contentAmount += folderRepository.findByFolderIdOrderBySortOrder(childFolder.getId()).size();
            	contentAmount += documentRepository.findByFolderIdOrderBySortOrder(childFolder.getId()).size();
            	if(contentAmount > 0) {
            		FolderData loadingNode = new FolderData();
            		loadingNode.setName("Loading...");
            		loadingNode.setExpanded(false);
            		childNode.getFolders().add(loadingNode);
            	}
            }
            else {
            	addFolders(childFolder, childNode, addedFiles, userId);
            	addDocuments(childFolder, childNode, addedFiles, userId);
            }
		}
	}

	private void addDocuments(Folder folder, FolderData node, Set<String> addedFiles, Integer userId) throws Exception {
		if(folder.getFolderId() != null) {
			FolderUserData folderUserData = folderUserDataRepository.findByFolderIdAndUserId(folder.getId(), userId);
			if(folderUserData == null || !folderUserData.getExpanded()) {
				return;
			}
		}

		List<Document> documents = documentRepository.findByFolderIdOrderBySortOrder(folder.getId());
		for (Document document : documents) {
			DocumentData documentData = readDocumentData(document);
			node.getDocuments().add(documentData);
		}
	}

	public DocumentData saveDocument(DocumentData documentData, Integer index) throws Exception {
		wazeraTool.checkForValidFileName(documentData.getName());

        Document document = null;
        boolean isTransient = documentData.getId() == null;
		if(!isTransient) {
			document = documentRepository.findById(documentData.getId()).orElse(null);
		}
		else {
			document = new Document();
			index = index != null ? index : 0;
		}
		document.setName(documentData.getName());
		document.setType(documentData.getType());
		document.setUserId(authService.getLoggedInUserId());
		document.setContent(documentData.getContent());
		document.setCreationDate(new Date());
		if(documentData.getParent() != null) {
			document.setFolderId(documentData.getParent().getId());
		}
        document = documentRepository.save(document);
        final int documentId = document.getId();
        documentData.setId(documentId);

		if(index != null) {
			sortDocuments(document, index);
		}

        List<DocumentTag> existingTags = documentTagRepository.findByDocumentId(documentId);
        List<String> existingTagValues = existingTags.stream()
        		.map(documentTag -> documentTag.getValue())
        		.collect(Collectors.toList());

        List<DocumentTag> documentTagsToDelete = documentData.getTags() == null ? Collections.emptyList() : existingTags.stream()
        		.filter(documentTag -> !documentData.getTags().contains(documentTag.getValue()))
        		.collect(Collectors.toList());
        documentTagRepository.deleteAll(documentTagsToDelete);

        List<DocumentTag> documentTagsToAdd = documentData.getTags() == null ? Collections.emptyList() : documentData.getTags().stream()
        		.filter(documentDataTag -> !existingTagValues.contains(documentDataTag))
        		.map(documentDataTag -> new DocumentTag(documentId, documentDataTag))
        		.collect(Collectors.toList());
        documentTagRepository.saveAll(documentTagsToAdd);

        if(isTransient && DocType.WORKFLOW.getId().equals(documentData.getType())) {
        	tasksService.saveNewWorkflow(documentData);
        }
        return documentData;
    }
	
	private void sortDocuments(Document document, Integer index) throws Exception {
		List<Folder> allFoldersInFolder = folderRepository.findByFolderIdOrderBySortOrder(document.getFolderId());
		List<Document> sortDocs = documentRepository.findByFolderIdOrderBySortOrder(document.getFolderId());
		
		Document foundDocument = null;
		for(Document sortDoc : sortDocs) {
			if(sortDoc.getId().equals(document.getId())) {
				foundDocument = sortDoc;
				break;
			}
		}
		if(foundDocument == null) {
			return;
		}
		sortDocs.remove(foundDocument);
		sortDocs.add(Math.max(0, Math.min(sortDocs.size(), index - allFoldersInFolder.size())), foundDocument);
		
		int currentId = 1;
		for(Document sortDoc : sortDocs) {
			sortDoc.setSortOrder(currentId);
			currentId++;
		}
		documentRepository.saveAll(sortDocs);
	}
	
	public void deleteDocument(Integer documentId) throws Exception {
		if(documentId == null) {
			return;
		}
		Document documentToDelete = documentRepository.findById(documentId).orElse(null);
		if(documentToDelete == null) {
			return;
		}
		documentRepository.delete(documentToDelete);
		List<DocumentTag> documentTagsToDelete = documentTagRepository.findByDocumentId(documentId);
		documentTagRepository.deleteAll(documentTagsToDelete);
		
		if(DocType.WORKFLOW.getId().equals(documentToDelete.getType())) {
        	tasksService.deleteWorkflow(documentId);
        }
	}

	private DocumentData readDocumentData(Document document) throws Exception {
		DocumentData documentData = new DocumentData();
		UserData userData = authService.findUserById(document.getUserId());

		documentData.setId(document.getId());
		documentData.setName(document.getName());
		documentData.setType(document.getType());
		documentData.setUser(userData == null ? null : userData.getUsername());
		documentData.setContent(document.getContent());
		documentData.setSortOrder(document.getSortOrder());
		documentData.setCreationDate(document.getCreationDate());

		List<DocumentTag> documentTags = documentTagRepository.findByDocumentId(document.getId());
		if(documentTags != null) {
			List<String> documentTagValues = documentTags.stream()
					.map(documentTag -> documentTag.getValue())
					.collect(Collectors.toList());
			documentData.setTags(documentTagValues);
		}
		else {
			documentData.setTags(new ArrayList<>());
		}

		return documentData;
	}
	
}
