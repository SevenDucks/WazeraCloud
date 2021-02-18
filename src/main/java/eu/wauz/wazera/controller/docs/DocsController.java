package eu.wauz.wazera.controller.docs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.WazeraTool;
import eu.wauz.wazera.controller.TasksController;
import eu.wauz.wazera.model.data.auth.Permission;
import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.docs.FolderData;
import eu.wauz.wazera.service.AuthDataService;
import eu.wauz.wazera.service.DocumentsDataService;
import eu.wauz.wazera.service.FoldersDataService;

@Controller
@Scope("view")
public class DocsController implements Serializable {

	private static final long serialVersionUID = -7261056043638925780L;

	@Autowired
	private DocumentsDataService documentsService;

	@Autowired
	private FoldersDataService foldersService;
	
	@Autowired
	private AuthDataService authService;
	
	@Autowired
	private TasksController tasksController;

	private TreeNode documentTree;
	
	private TreeNode selectedNode;

	private FolderData rootNodeData;
	
	private List<String> documentTags;
	
	private List<String> searchTags;
	
	private String inputName;
	
	private String type;
	
	private String content;
	
	private boolean allowEditing;
	
	private boolean allowSorting;

	private Integer docId;
	
	private WazeraTool wazeraTool;

	@PostConstruct
	private void init() {
		wazeraTool = new WazeraTool();
		
		Object doctIdObject = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		if(doctIdObject != null)
		{
			String docIdString = (String) doctIdObject;
			docId = Integer.valueOf(docIdString);
		}
	}

	public TreeNode getDocumentTree() {
		if (documentTree == null) {
			selectTree();
		}
		return documentTree;
	}

	private void addFolderNodes(FolderData folderNode, TreeNode treeNode, boolean isRootNode) {
		FolderTreeNode node = null;
		if (isRootNode) {
			node = new RootFolderTreeNode(folderNode, treeNode);
			node.setExpanded(true);
		}
		else {
			node = new FolderTreeNode(folderNode, treeNode);
			node.setExpanded(folderNode.isExpanded() != null ? folderNode.isExpanded() : false);
		}
		
		for (FolderData childNode : folderNode.getFolders()) {
			addFolderNodes(childNode, node, false);
		}
		for (DocumentData childNode : folderNode.getDocuments()) {
			addDocumentNodes(childNode, node);
		}
	}

	private void addDocumentNodes(DocumentData documentNode, TreeNode treeNode) {
		DocumentTreeNode node = new DocumentTreeNode(documentNode, treeNode);
		node.setExpanded(true);
		
		if(Objects.equals(documentNode.getId(), docId)) {
			node.setSelected(true);
			setSelectedNode(node);
			docId = null;
		}
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;

		if (selectedNode == null) {
			return;
		}
		if (selectedNode instanceof DocumentTreeNode) {
			DocumentData documentData = ((DocumentTreeNode) selectedNode).getDocumentData();
			inputName = documentData.getName();
			content = documentData.getContent();
			documentTags = documentData.getTags();
			if("workflowNode".equals(selectedNode.getType())) {
				tasksController.setWorkflowFromDocument(documentData);
			}
		}
		else {
			inputName = ((FolderTreeNode) selectedNode).getName();
			content = "";
			documentTags = new ArrayList<>();
		}
		
		PrimeFaces.current().executeScript("history.pushState({}, null, '" + getDocumentLink() + "');");
	}

	public void addDirectoryNode() {
		FolderTreeNode parent = (FolderTreeNode) selectedNode;

		FolderData folderData = new FolderData();
		folderData.setName(inputName);
		folderData.setParent(parent.getFolderData());

		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new FolderTreeNode(folderData, selectedNode);
		newNode.setExpanded(true);
		setSelectedNode(newNode);

		documentTree = null;
		inputName = "";
	}
	
	public void addDocumentNode() {
		FolderTreeNode parent = (FolderTreeNode) selectedNode;

		DocumentData documentData = new DocumentData();
		documentData.setName(inputName);
		documentData.setType(type);
		documentData.setContent("");
		documentData.setParent(parent.getFolderData());

		try {
			documentData = documentsService.saveDocument(documentData, null);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new DocumentTreeNode(documentData, selectedNode);
		newNode.setExpanded(true);
		setSelectedNode(newNode);

		documentTree = null;
		inputName = "";
	}
	
	public void copyDocumentNode() {
		DocumentTreeNode node = (DocumentTreeNode) selectedNode;

		DocumentData dataToCopy = node.getDocumentData();
		DocumentData documentData = new DocumentData();
		documentData.setName(inputName);
		documentData.setType(dataToCopy.getType());
		documentData.setContent(dataToCopy.getContent());
		documentData.setParent(((FolderTreeNode) node.getParent()).getFolderData());
		documentData.setTags(dataToCopy.getTags());

		try {
			documentData = documentsService.saveDocument(documentData, null);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new DocumentTreeNode(documentData, selectedNode);
		newNode.setExpanded(true);
		setSelectedNode(newNode);

		documentTree = null;
		inputName = "";
	}

	public void renameDirectoryNode() {
		FolderTreeNode selectedFolderData = (FolderTreeNode) selectedNode;
		selectedFolderData.getFolderData().setName(inputName);

		try {
			foldersService.saveFolder(selectedFolderData.getFolderData(), null);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		documentTree = null;
		inputName = "";
	}

	public void renameDocumentNode() {
		DocumentTreeNode selectedDocumentData = (DocumentTreeNode) selectedNode;
		selectedDocumentData.getDocumentData().setName(inputName);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		documentTree = null;
		inputName = "";
	}
	
	public List<FileSystem> getFileSystems() {
		return FileSystem.findAll();
	}
	
	public boolean showDashboard() {
		return selectedNode == null || selectedNode.getType().equals("directoryNode") || selectedNode.getType().equals("rootNode");
	}

	public boolean showEditor() {
		return selectedNode != null && selectedNode.getType().equals("documentNode");
	}
	
	public boolean showWorkflow() {
		return selectedNode != null && selectedNode.getType().equals("workflowNode");
	}
	
	public boolean showButtonBar() {
		return (showEditor() && canViewDocuments()) || (showWorkflow() && canViewWorkflows());
	}

	public void saveDocument(boolean exit) {
		DocumentTreeNode selectedDocumentData = (DocumentTreeNode) selectedNode;

		selectedDocumentData.getDocumentData().setContent(content);
		selectedDocumentData.getDocumentData().setTags(documentTags);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null);
			wazeraTool.showInfoMessage("Your Document was saved!");
			allowEditing = !exit;
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}
	
	public void showInfoMessage(String infoMessage) {
		wazeraTool.showInfoMessage(infoMessage);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public boolean isAllowEditing() {
		return allowEditing;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

	public boolean isAllowSorting() {
		return allowSorting;
	}

	public void setAllowSorting(boolean allowSorting) {
		this.allowSorting = allowSorting;
	}

	public void selectTree() {
		documentTree = new DefaultTreeNode("documentTree", null);

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}
		if(searchTags.size() == 0 && selectedNode instanceof DocumentTreeNode) {
			docId = selectedNode != null ? ((DocumentTreeNode) selectedNode).getDocumentData().getId() : 0;
		}

		try {
			rootNodeData = documentsService.getDocuments(foldersService.getRootFolder().getId(), docId, searchTags);
			addFolderNodes(rootNodeData, documentTree, true);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void onNodeExpand(NodeExpandEvent event) {
		if(hasSearchTags()) {
			return;
		}

		FolderData folderData = ((FolderTreeNode) event.getTreeNode()).getFolderData();
		folderData.setExpanded(true);
		try {
			if(folderData.getId() != null) {
				foldersService.saveFolder(folderData, null);
			}
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}

		FolderData result = null;
		try {
			result = documentsService.getDocuments(folderData.getId(), null, searchTags);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		FolderData expandedFolderData = result;
		TreeNode treeNode = event.getTreeNode();

		while(treeNode.getChildCount() != 0) {
			treeNode.getChildren().remove(0);
		}

		for (FolderData childNode : expandedFolderData.getFolders()) {
			addFolderNodes(childNode, treeNode, false);
		}
		for (DocumentData childNode : expandedFolderData.getDocuments()) {
			addDocumentNodes(childNode, treeNode);
		}
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		collapse(event.getTreeNode());
	}

	public void collapseAll() {
		collapseRecursive(documentTree);
	}

	public void collapseRecursive(TreeNode parentNode) {
		for(TreeNode childNode : parentNode.getChildren()) {
			collapseRecursive(childNode);
		}
		collapse(parentNode);
	}

	public void collapse(TreeNode treeNode) {
		if(hasSearchTags() || !(treeNode instanceof FolderTreeNode)) {
			return;
		}
		
		treeNode.setExpanded(false);
		
		FolderData folderData = ((FolderTreeNode)treeNode).getFolderData();
		folderData.setExpanded(false);
		try {
			if(folderData.getId() != null) {
				foldersService.saveFolder(folderData, null);
			}
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}

	public void deleteFolder() {
		FolderTreeNode node = (FolderTreeNode) selectedNode;

		try {
			foldersService.deleteFolder(node.getFolderData());
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}

		selectTree();
	}
	
	public void deleteDocument() {
		DocumentTreeNode node = (DocumentTreeNode) selectedNode;
		
		try {
			documentsService.deleteDocument(node.getDocumentData().getId());
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
		
		selectTree();
	}

	public void onDragDrop(TreeDragDropEvent event) {
		TreeNode dragNode = event.getDragNode();
		TreeNode dropNode = event.getDropNode();
		int dropIndex = event.getDropIndex();

		try {
			if (dropNode instanceof FolderTreeNode) {
				FolderTreeNode dropFolderNode = (FolderTreeNode) dropNode;
				if (dragNode instanceof FolderTreeNode) {
					FolderTreeNode dragFolderNode = (FolderTreeNode) dragNode;
					dragFolderNode.getFolderData().setParent(dropFolderNode.getFolderData());
					foldersService.saveFolder(dragFolderNode.getFolderData(), dropIndex);
				}
				else if (dragNode instanceof DocumentTreeNode) {
					DocumentTreeNode dragDocumentNode = (DocumentTreeNode) dragNode;
					dragDocumentNode.getDocumentData().setParent(dropFolderNode.getFolderData());
					documentsService.saveDocument(dragDocumentNode.getDocumentData(), dropIndex);
				}
			}
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
		selectTree();
	}
	
	public String getName() {
		return inputName;
	}

	public void setName(String name) {
		this.inputName = name;
	}

	public void setName(String name, String type) {
		this.inputName = name;
		this.type = type;
	}

	private boolean hasSearchTags() {
		return searchTags != null && !searchTags.isEmpty();
	}

	public List<String> getTags() {
		return documentTags;
	}

	public void setTags(List<String> tags) {
		this.documentTags = tags == null ? new ArrayList<>() : tags;
	}

	public List<String> getSearchTags() {
		return searchTags;
	}

	public void setSearchTags(List<String> searchTags) {
		this.searchTags = searchTags;
	}

    public String getDocumentLink() {
    	try {
    		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    		String contextPath = req.getContextPath();
    		String baseUrl = StringUtils.substringBefore(req.getRequestURL().toString(), contextPath) + contextPath;
    		if(selectedNode instanceof DocumentTreeNode) {
				Integer docId = selectedNode != null ? ((DocumentTreeNode) selectedNode).getDocumentData().getId() : 0;
				return baseUrl + "/docs.xhtml?docId=" + docId;
    		}
    		return baseUrl + "/docs.xhtml";
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
			return "";
		}
    }
    
    public boolean canEditFolders() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.EDIT_FOLDERS.getId());
	}
    
    public boolean canDeleteFolders() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.DELETE_FOLDERS.getId());
	}
    
    public boolean canViewDocuments() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.VIEW_DOCUMENTS.getId());
	}
    
    public boolean canEditDocuments() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.EDIT_DOCUMENTS.getId());
	}
    
    public boolean canDeleteDocuments() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.DELETE_DOCUMENTS.getId());
	}
    
    public boolean canViewWorkflows() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.VIEW_WORKFLOWS.getId());
	}
    
    public boolean canEditWorkflows() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.EDIT_WORKFLOWS.getId());
	}
    
    public boolean canDeleteWorkflows() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.DELETE_WORKFLOWS.getId());
	}

}
