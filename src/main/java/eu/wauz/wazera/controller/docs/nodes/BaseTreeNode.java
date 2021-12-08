package eu.wauz.wazera.controller.docs.nodes;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocType;

public class BaseTreeNode extends DefaultTreeNode {

	private static final long serialVersionUID = 3153292562646655280L;
	
    protected BaseTreeNode(DocType nodeType, String name, TreeNode parent) {
        super(nodeType.getId(), new BaseTreeNodeMeta(name, nodeType), parent);
    }
    
    public void init() {
    	((BaseTreeNodeMeta) getData()).setNode(this);
    }
    
    @Override
	public String toString() {
		return ((BaseTreeNodeMeta) getData()).getName();
	}
    
	public static class BaseTreeNodeMeta {
		
		private BaseTreeNode node;
		
		private final String name;
		
		private final DocType type;
		
		public BaseTreeNodeMeta(String name, DocType type) {
			this.name = name;
			this.type = type;
		}
		
		public final BaseTreeNode getNode() {
			return node;
		}
		
		public final void setNode(BaseTreeNode node) {
			this.node = node;
		}
		
		public final String getName() {
			return name;
		}
		
		public final DocType getType() {
			return type;
		}
		
	}
	
}
