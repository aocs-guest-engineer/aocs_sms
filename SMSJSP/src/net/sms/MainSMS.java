package net.sms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.smslib.AGateway;
import org.smslib.AGateway.GatewayStatuses;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.ICallNotification;
import org.smslib.IGatewayStatusNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Library;
import org.smslib.Message.MessageTypes;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.crypto.AESKey;
import org.smslib.modem.SerialModemGateway;

public class MainSMS {
	
	private SerialModemGateway gateway;
	
	private InboundNotification inboundNotification;
	
	private CallNotification callNotification;
	
	private GatewayStatusNotification statusNotification;
	
	private OrphanedMessageNotification orphanedMessageNotification;
	
	private OutboundNotification outboundNotification;
	
	//Array to store all sms messages
	private ArrayList<InboundMessage> smsMsgList;
	
	
	public MainSMS() {
		
		//initialize all 
		try {
			initialize();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SMSLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void initialize() throws TimeoutException, SMSLibException, IOException, InterruptedException {
		
		//create the array that store all the messages
		smsMsgList = new ArrayList<InboundMessage>();

		// Create the Gateway representing the serial GSM modem. Only change your port
		gateway = new SerialModemGateway( "modem.com4", "COM4", 115200, "Huawei", "E160" );
		// Set the modem protocol to PDU (alternative is TEXT). PDU is the default, anyway...
		gateway.setProtocol( Protocols.PDU );
		// Do we want the Gateway to be used for Inbound messages?
		gateway.setInbound( true );
		// Do we want the Gateway to be used for Outbound messages?
		gateway.setOutbound( true );
		// Let SMSLib know which is the SIM PIN.
		gateway.setSimPin( "0000" );
		
		
		// Set up the notification methods.
		inboundNotification = new InboundNotification();
		callNotification = new CallNotification();
		statusNotification = new GatewayStatusNotification();
		orphanedMessageNotification = new OrphanedMessageNotification();
		
		Service.getInstance().setInboundMessageNotification(inboundNotification);
		Service.getInstance().setCallNotification(callNotification);
		Service.getInstance().setGatewayStatusNotification(statusNotification);
		Service.getInstance().setOrphanedMessageNotification(orphanedMessageNotification);
		Service.getInstance().setOutboundMessageNotification(outboundNotification);

		//Add modem gateway to service
		Service.getInstance().addGateway(gateway);
		
		//Start the service
		Service.getInstance().startService();
		
		//Service.getInstance().readMessages(msgList, MessageClasses.ALL);
		//Service.getInstance().stopService();

	}
	
	//Pass one sms sending request to the queue
	public void sendSMS( String phone, String message ) {
		
		Service.getInstance().queueMessage(new OutboundMessage( phone, message ));
	}
	
	//Pass multiple sms sending request to the queue
	public void multiSendSMS( HashMap<String, String> multi_list ) {
		
		for ( Map.Entry<String, String> entry : multi_list.entrySet() ) {
			Service.getInstance().queueMessage( new OutboundMessage( entry.getKey(), entry.getValue() ) );
		}
	}
	
	
	
	//Check for repeated message via PDU number
	boolean checkPDU( String pdu ) {
			
		boolean is_exist = false;
		
		for ( InboundMessage m : smsMsgList ) {
			
			if ( m.getPduUserData().compareTo( pdu ) == 0 ) {
				
				is_exist = true;
				break;
			}
		}
		return is_exist;
	}

	public class InboundNotification implements IInboundMessageNotification {
		
		public void process(AGateway gateway, MessageTypes msgType, InboundMessage msg) {
			
			if ( msgType == MessageTypes.INBOUND ) {
				
				if ( !checkPDU( msg.getPduUserData() ) ) {
					System.out.println(">>> New Inbound message detected from Gateway: " + gateway.getGatewayId());
					System.out.println(msg);
					smsMsgList.add( msg );
				}
				
			}
			else if (msgType == MessageTypes.STATUSREPORT) System.out.println(">>> New Inbound Status Report message detected from Gateway: " + gateway.getGatewayId());
		}
	}

	public class CallNotification implements ICallNotification
	{
		public void process(AGateway gateway, String callerId)
		{
			System.out.println(">>> New call detected from Gateway: " + gateway.getGatewayId() + " : " + callerId);
		}
	}

	public class GatewayStatusNotification implements IGatewayStatusNotification
	{
		public void process(AGateway gateway, GatewayStatuses oldStatus, GatewayStatuses newStatus)
		{
			System.out.println(">>> Gateway Status change for " + gateway.getGatewayId() + ", OLD: " + oldStatus + " -> NEW: " + newStatus);
		}
	}

	public class OrphanedMessageNotification implements IOrphanedMessageNotification
	{
		public boolean process(AGateway gateway, InboundMessage msg)
		{
			System.out.println(">>> Orphaned message part detected from " + gateway.getGatewayId());
			System.out.println(msg);
			// Since we are just testing, return FALSE and keep the orphaned message part.
			return false;
		}
	}
	
	public class OutboundNotification implements IOutboundMessageNotification
	{
		public void process(AGateway gateway, OutboundMessage msg)
		{
			System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
			System.out.println(msg);
			
		}
	}
	
	
	//getter
	public ArrayList<InboundMessage> getSmsMsgList() {
		
		return this.smsMsgList;
	}

}