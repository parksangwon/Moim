package com.navercorp.board.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Address { // 주소찾기 api를 사용할때 쓰는 VO
	private String inputYn;
	private String roadFullAddr;
	private String roadAddrPart1;
	private String roadAddrPart2;
	private String engAddr;
	private String jibunAddr;
	private String zipNo;
	private String addrDetail;
	private String admCd;
	private String rnMgtSn;
	private String bdMgtSn;
	private String detBdNmList;
	private String bdNm;
	private String bdKdcd;
	private String siNm;
	private String sggNm;
	private String emdNm;
	private String liNm;
	private String rn;
	private String udrtYn;
	private String buldMnnm;
	private String buldSlno;
	private String mtYn;
	private String lnbrMnnm;
	private String lnbrSlno;
	private String emdNo;
}
