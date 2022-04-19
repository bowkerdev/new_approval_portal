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
 * @version 2022-04-19
 */
@Data
public class OaMisPolicyConfig extends DataEntity<OaMisPolicyConfig> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="department", align=2, sort=1)
	private String department;		// department
	@ExcelField(title="hardware", dictType="yes_no", align=2, sort=2)
	private String hardware;		// hardware
	@ExcelField(title="lan_connection", dictType="yes_no", align=2, sort=3)
	private String lanConnection;		// lan_connection
	@ExcelField(title="wifi_office", dictType="yes_no", align=2, sort=4)
	private String wifiOffice;		// wifi_office
	@ExcelField(title="wifi_guest", dictType="yes_no", align=2, sort=5)
	private String wifiGuest;		// wifi_guest
	@ExcelField(title="wifi_others", dictType="yes_no", align=2, sort=6)
	private String wifiOthers;		// wifi_others
	@ExcelField(title="vpn", dictType="yes_no", align=2, sort=7)
	private String vpn;		// vpn
	@ExcelField(title="usb_drive", dictType="yes_no", align=2, sort=8)
	private String usbDrive;		// usb_drive
	@ExcelField(title="share_drive", dictType="yes_no", align=2, sort=9)
	private String shareDrive;		// share_drive
	@ExcelField(title="ms_team_drive", dictType="yes_no", align=2, sort=10)
	private String msTeamDrive;		// ms_team_drive
	@ExcelField(title="threerd_party_cloud_drive", dictType="yes_no", align=2, sort=11)
	private String threerdPartyCloudDrive;		// threerd_party_cloud_drive
	@ExcelField(title="internet", dictType="yes_no", align=2, sort=12)
	private String internet;		// internet
	@ExcelField(title="instant_massager", dictType="yes_no", align=2, sort=13)
	private String instantMassager;		// instant_massager
	@ExcelField(title="cloud_transfer", dictType="yes_no", align=2, sort=14)
	private String cloudTransfer;		// cloud_transfer
	@ExcelField(title="one_drive_personal", dictType="yes_no", align=2, sort=15)
	private String oneDrivePersonal;		// one_drive_personal
	@ExcelField(title="ms_team_message", dictType="yes_no", align=2, sort=16)
	private String msTeamMessage;		// ms_team_message
	@ExcelField(title="software_installation", dictType="yes_no", align=2, sort=17)
	private String softwareInstallation;		// software_installation
	@ExcelField(title="ms_email_to_external_address", dictType="yes_no", align=2, sort=18)
	private String msEmailToExternalAddress;		// ms_email_to_external_address
	@ExcelField(title="mfa", dictType="yes_no", align=2, sort=19)
	private String mfa;		// mfa
	@ExcelField(title="mobile_email", dictType="yes_no", align=2, sort=20)
	private String mobileEmail;		// mobile_email
	
	public OaMisPolicyConfig() {
		super();
	}
	
	public OaMisPolicyConfig(String id){
		super(id);
	}
}