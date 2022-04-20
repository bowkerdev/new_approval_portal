/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.oa.mispolicy.entity;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * MIS PolicyEntity
 * @author zhimi
 * @version 2022-04-20
 */
@Data
public class OaMisPolicyInst extends DataEntity<OaMisPolicyInst> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="site", align=2, sort=1)
	private String site;		// site
	@ExcelField(title="department", align=2, sort=2)
	private String department;		// department
	@ExcelField(title="userids", align=2, sort=3)
	private String userids;		// userids
	@ExcelField(title="hardware", align=2, sort=4)
	private String hardware;		// hardware
	@ExcelField(title="lan_connection", align=2, sort=5)
	private String lanConnection;		// lan_connection
	@ExcelField(title="wifi_office", align=2, sort=6)
	private String wifiOffice;		// wifi_office
	@ExcelField(title="wifi_guest", align=2, sort=7)
	private String wifiGuest;		// wifi_guest
	@ExcelField(title="wifi_others", align=2, sort=8)
	private String wifiOthers;		// wifi_others
	@ExcelField(title="vpn", align=2, sort=9)
	private String vpn;		// vpn
	@ExcelField(title="usb_drive", align=2, sort=10)
	private String usbDrive;		// usb_drive
	@ExcelField(title="share_drive", align=2, sort=11)
	private String shareDrive;		// share_drive
	@ExcelField(title="ms_team_drive", align=2, sort=12)
	private String msTeamDrive;		// ms_team_drive
	@ExcelField(title="threerd_party_cloud_drive", align=2, sort=13)
	private String threerdPartyCloudDrive;		// threerd_party_cloud_drive
	@ExcelField(title="internet", align=2, sort=14)
	private String internet;		// internet
	@ExcelField(title="instant_massager", align=2, sort=15)
	private String instantMassager;		// instant_massager
	@ExcelField(title="cloud_transfer", align=2, sort=16)
	private String cloudTransfer;		// cloud_transfer
	@ExcelField(title="one_drive_personal", align=2, sort=17)
	private String oneDrivePersonal;		// one_drive_personal 
	@ExcelField(title="ms_team_message", align=2, sort=18)
	private String msTeamMessage;		// ms_team_message
	@ExcelField(title="software_installation", align=2, sort=19)
	private String softwareInstallation;		// software_installation
	@ExcelField(title="ms_email_to_external_address", align=2, sort=20)
	private String msEmailToExternalAddress;		// ms_email_to_external_address
	@ExcelField(title="mfa", align=2, sort=21)
	private String mfa;		// mfa
	@ExcelField(title="mobile_email", align=2, sort=22)
	private String mobileEmail;		// mobile_email
	@ExcelField(title="requirements", align=2, sort=23)
	private String requirements;		// requirements
	
	public OaMisPolicyInst() {
		super();
	}

	public OaMisPolicyInst(String id){
		super(id);
	}
}