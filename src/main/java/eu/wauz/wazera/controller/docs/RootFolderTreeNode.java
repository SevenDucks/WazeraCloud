package eu.wauz.wazera.controller.docs;

import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;
import eu.wauz.wazera.model.data.docs.FolderData;

public class RootFolderTreeNode extends FolderTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;

    public RootFolderTreeNode(FolderData folderData, TreeNode parent) {
        super(DocType.ROOT, "Dashboard", folderData, parent);
    }

}
