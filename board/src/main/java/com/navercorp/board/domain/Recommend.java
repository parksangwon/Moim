package com.navercorp.board.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommends",
	   uniqueConstraints={
		   @UniqueConstraint(
			   columnNames={"groupId", "userId"}
		   )
	   })
@Data
@NoArgsConstructor
public class Recommend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long recommendId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "groupId")
	private Group group;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	public Recommend(User user, Group group) {
		this.user = user;
		this.group = group;
		
		user.getRecommendList().add(this);
		group.getRecommendList().add(this);
	}
}
