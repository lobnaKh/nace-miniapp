package com.nace.miniapp.model;

import java.io.Serializable;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Nace")
@Entity
public class Nace implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private int id;

	@Column(name = "ORDER_ID", unique = true)
	private long order;

	@Column(name = "LEVEL")
	private int level;

	@Column(name = "CODE")
	private String code;

	@Column(name = "PARENT")
	private String parent;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "INCLUDING",columnDefinition="LONGTEXT")
	private String including;

	@Column(name = "INCLUDING_MORE", columnDefinition="LONGTEXT")
	private String includingMore;

	@Column(name = "RULING",columnDefinition="LONGTEXT")
	private String rulings;

	@Column(name = "EXCLUDING", columnDefinition="LONGTEXT")
	private String excluding;

	@Column(name = "REFERENCE", columnDefinition="LONGTEXT")
	private String reference;

}
