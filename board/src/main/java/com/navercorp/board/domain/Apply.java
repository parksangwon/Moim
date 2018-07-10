package com.navercorp.board.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "applys",
	   uniqueConstraints={
		   @UniqueConstraint(
				columnNames={"groupId","userId"}
		   )
	   })
@Data
@NoArgsConstructor
public class Apply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long applyId;

	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private LocalDateTime applyTime;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "groupId")
	private Group group;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;
	
	public Apply(Group group, User user) {
		this.group = group;
		this.user = user;
		
		group.getApplyList().add(this);
		user.getApplyList().add(this);
	}
}
