package eu.wauz.wazera.controller.docs;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocumentData;

public class DocumentTreeNode extends DefaultTreeNode {

	private static final long serialVersionUID = -2541631707902646133L;

	private DocumentData documentData;

	public DocumentTreeNode(DocumentData documentData, TreeNode parent) {
		super(documentData.getType() != null ? documentData.getType() : "documentNode", documentData.getName(), parent);
		this.documentData = documentData;
	}

    public String getName() {
    	return String.valueOf(getData());
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
