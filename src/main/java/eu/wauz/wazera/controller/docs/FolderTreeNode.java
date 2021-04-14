package eu.wauz.wazera.controller.docs;

import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;
import eu.wauz.wazera.model.data.docs.FolderData;

public class FolderTreeNode extends BaseTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;

    private FolderData folderData;
    
    public FolderTreeNode(FolderData folderData, TreeNode parent) {
        super(DocType.DIRECTORY, folderData.getName(), parent);
        this.folderData = folderData;
    }
    
    public FolderTreeNode(DocType nodeType, String name, FolderData folderData, TreeNode parent) {
        super(nodeType, name, parent);
        this.folderData = folderData;
    }

	public FolderData getFolderData() {
		return folderData;
	}

}
