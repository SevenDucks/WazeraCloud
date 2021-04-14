package eu.wauz.wazera.controller.docs;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;

public class BaseTreeNode extends DefaultTreeNode {

	private static final long serialVersionUID = 3153292562646655280L;
	
    public BaseTreeNode(DocType nodeType, String name, TreeNode parent) {
        super(nodeType.getId(), new BaseTreeNodeMeta(name, nodeType), parent);
    }
    
    @Override
	public String toString() {
		return ((BaseTreeNodeMeta) getData()).getName();
	}
    
	public static class BaseTreeNodeMeta {
		
		private final String name;
		
		private final DocType type;
		
		public BaseTreeNodeMeta(String name, DocType type) {
			this.name = name;
			this.type = type;
		}
		
		public final String getName() {
			return name;
		}
		
		public final DocType getType() {
			return type;
		}
		
	}
	
}
