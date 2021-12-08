package eu.wauz.wazera.controller.docs.nodes;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;
import eu.wauz.wazera.model.data.docs.DocumentData;

public class DocumentTreeNode extends BaseTreeNode {

	private static final long serialVersionUID = -2541631707902646133L;

	private DocumentData documentData;
	
	public static DocumentTreeNode create(DocumentData documentData, TreeNode parent) {
		DocumentTreeNode node = new DocumentTreeNode(documentData, parent);
		node.init();
		return node;
	}
	
	protected DocumentTreeNode(DocumentData documentData, TreeNode parent) {
		super(DocType.getFromString(documentData.getType()), documentData.getName(), parent);
		this.documentData = documentData;
	}
    
    public String getUser() {
    	return StringUtils.isBlank(documentData.getUser()) ? "unknown" : documentData.getUser();
    }
    
    public String getDate() {
    	return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(documentData.getCreationDate());
    }
    
    public int getWordCount() {
    	String content = getDocumentData().getContent();
    	return content == null ? 0 : Arrays.asList(content.replace("</p>", " ").replaceAll("\\<.*?\\>", "").split(" ")).stream()
    			.filter(word -> StringUtils.isNotBlank(word))
    			.collect(Collectors.toList())
    			.size();
    }

	public DocumentData getDocumentData() {
		return documentData;
	}

}
