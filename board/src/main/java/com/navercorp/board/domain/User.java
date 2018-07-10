package com.navercorp.board.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String email;

	@JsonIgnore
	@Column(name = "encodedPw", nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = true)
	private String company;

	@Column(nullable = true)
	private String introduction;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime joinDate;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	@OrderBy("applyTime asc")
	private List<Apply> applyList = new ArrayList<Apply>();

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Comment> commentList = new ArrayList<Comment>();

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Recommend> recommendList = new ArrayList<Recommend>();

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	@OrderBy("createTime asc")
	private List<Group> groupList = new ArrayList<Group>();

	public void update(User newInfoUser) {
		this.name = newInfoUser.getName();
		this.phone = newInfoUser.getPhone();
		this.company = newInfoUser.getCompany();
		this.introduction = newInfoUser.getIntroduction();
	}
}
