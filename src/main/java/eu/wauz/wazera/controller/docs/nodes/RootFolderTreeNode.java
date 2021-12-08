package eu.wauz.wazera.controller.docs.nodes;

import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;
import eu.wauz.wazera.model.data.docs.FolderData;

public class RootFolderTreeNode extends FolderTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;
    
    public static RootFolderTreeNode create(FolderData folderData, TreeNode parent) {
    	RootFolderTreeNode node = new RootFolderTreeNode(folderData, parent);
    	node.init();
    	return node;
    }

    protected RootFolderTreeNode(FolderData folderData, TreeNode parent) {
        super(DocType.ROOT, "Dashboard", folderData, parent);
    }

}
