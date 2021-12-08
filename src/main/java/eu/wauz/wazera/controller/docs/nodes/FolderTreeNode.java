package eu.wauz.wazera.controller.docs.nodes;

import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;
import eu.wauz.wazera.model.data.docs.FolderData;

public class FolderTreeNode extends BaseTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;

    private FolderData folderData;
    
    public static FolderTreeNode create(FolderData folderData, TreeNode parent) {
    	FolderTreeNode node = new FolderTreeNode(folderData, parent);
    	node.init();
    	return node;
    }
    
    protected FolderTreeNode(FolderData folderData, TreeNode parent) {
        super(DocType.DIRECTORY, folderData.getName(), parent);
        this.folderData = folderData;
    }
    
    protected FolderTreeNode(DocType nodeType, String name, FolderData folderData, TreeNode parent) {
        super(nodeType, name, parent);
        this.folderData = folderData;
    }

	public FolderData getFolderData() {
		return folderData;
	}

}
