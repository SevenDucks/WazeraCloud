package eu.wauz.wazera.model.entity.tasks;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Workflow")
public class Workflow implements Serializable {

	private static final long serialVersionUID = 5640434342468315408L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer documentId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	@Override
	public String toString() {
		return "Workflow(" + (getId() != null ? String.valueOf(getId()) : "transient") + ")";
	}

}
