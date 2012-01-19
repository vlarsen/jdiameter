/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.slee.resources.diameter.tests.factories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoAnswer;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationAnswer;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;
import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.avp.ReasonCode;
import net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Stack;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.CxDxAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl;
import org.mobicents.slee.resource.diameter.cxdx.CxDxMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.cxdx.CxDxServerSessionImpl;
import org.mobicents.slee.resources.diameter.tests.factories.CCAFactoriesTest.MyConfiguration;

/**
 * Test class for JAIN SLEE Diameter Cx/Dx RA Message and AVP Factories
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxFactoriesTest {

  private static CxDxMessageFactory cxdxMessageFactory;
  private static CxDxAVPFactory cxdxAvpFactory;

  private static Stack stack;

  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    cxdxAvpFactory = new CxDxAVPFactoryImpl(new DiameterAvpFactoryImpl());
    try {
      cxdxMessageFactory = new CxDxMessageFactoryImpl(new DiameterMessageFactoryImpl(stack), stack);

      CxDxSessionFactoryImpl sf = new CxDxSessionFactoryImpl(stack.getSessionFactory());
      ApplicationId cxdxAppId = ApplicationId.createByAuthAppId(DiameterCxDxAvpCodes.CXDX_VENDOR_ID, DiameterCxDxAvpCodes.CXDX_AUTH_APP_ID);
      org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl stackServerSession = (org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl) sf.getNewSession("123", ServerCxDxSession.class, cxdxAppId, new Object[0]);
      org.jdiameter.client.impl.app.cxdx.CxDxClientSessionImpl stackClientSession = (org.jdiameter.client.impl.app.cxdx.CxDxClientSessionImpl) sf.getNewSession("321", ClientCxDxSession.class, cxdxAppId, new Object[0]);
      serverSession = new CxDxServerSessionImpl(cxdxMessageFactory, cxdxAvpFactory, stackServerSession, stackServerSession, null, null, stack);
      clientSession = new CxDxClientSessionImpl(cxdxMessageFactory, cxdxAvpFactory, stackClientSession, stackClientSession, null, null, null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      AvpDictionary.INSTANCE.parseDictionary(CxDxFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  private static CxDxServerSessionImpl serverSession;
  private static CxDxClientSessionImpl clientSession;
  
  @Test
  public void isRequestLIR() throws Exception {
    LocationInfoRequest lir = cxdxMessageFactory.createLocationInfoRequest();
    assertTrue("Request Flag in Location-Info-Request is not set.", lir.getHeader().isRequest());
  }

  @Test
  public void isProxiableLIR() throws Exception {
    LocationInfoRequest lir = cxdxMessageFactory.createLocationInfoRequest();
    assertTrue("The 'P' bit is not set by default in Location-Info-Request, it should.", lir.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersLIR() throws Exception {
    LocationInfoRequest lir = cxdxMessageFactory.createLocationInfoRequest();

    int nFailures = AvpAssistant.testMethods(lir, LocationInfoRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isAnswerLIA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createLocationInfoRequest());
    LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
    
    assertFalse("Request Flag in Location-Info-Answer is set.", lia.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersLIA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createLocationInfoRequest());
    LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();

    int nFailures = AvpAssistant.testMethods(lia, LocationInfoAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostLIA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createLocationInfoRequest());
    LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", lia.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmLIA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createLocationInfoRequest());
    LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", lia.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedLIA() throws Exception {
    LocationInfoRequest lir = cxdxMessageFactory.createLocationInfoRequest();
    serverSession.fetchSessionData(lir);
    LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", lir.getHeader().isProxiable(), lia.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) lir).getGenericData().setProxiable(!lir.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Location-Info-Request, it should.", lir.getHeader().isProxiable() != lia.getHeader().isProxiable());
    serverSession.fetchSessionData(lir);

    lia = serverSession.createLocationInfoAnswer();
    assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", lir.getHeader().isProxiable(), lia.getHeader().isProxiable());
  }

  @Test
  public void isRequestMAR() throws Exception {
    MultimediaAuthenticationRequest mar = cxdxMessageFactory.createMultimediaAuthenticationRequest();
    assertTrue("Request Flag in Multimedia-Authentication-Request is not set.", mar.getHeader().isRequest());
  }

  @Test
  public void isProxiableMAR() throws Exception {
    MultimediaAuthenticationRequest mar = cxdxMessageFactory.createMultimediaAuthenticationRequest();
    assertTrue("The 'P' bit is not set by default in Multimedia-Authentication-Request, it should.", mar.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersMAR() throws Exception {
    MultimediaAuthenticationRequest mar = cxdxMessageFactory.createMultimediaAuthenticationRequest();

    int nFailures = AvpAssistant.testMethods(mar, MultimediaAuthenticationRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isAnswerMAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createMultimediaAuthenticationRequest());
    MultimediaAuthenticationAnswer maa = serverSession.createMultimediaAuthenticationAnswer();
    
    assertFalse("Request Flag in Multimedia-Authentication-Answer is set.", maa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersMAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createMultimediaAuthenticationRequest());
    MultimediaAuthenticationAnswer maa = serverSession.createMultimediaAuthenticationAnswer();

    int nFailures = AvpAssistant.testMethods(maa, MultimediaAuthenticationAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostMAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createMultimediaAuthenticationRequest());
    MultimediaAuthenticationAnswer maa = serverSession.createMultimediaAuthenticationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", maa.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmMAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createMultimediaAuthenticationRequest());
    MultimediaAuthenticationAnswer maa = serverSession.createMultimediaAuthenticationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", maa.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedMAA() throws Exception {
    MultimediaAuthenticationRequest mar = cxdxMessageFactory.createMultimediaAuthenticationRequest();
    serverSession.fetchSessionData(mar);
    MultimediaAuthenticationAnswer maa = serverSession.createMultimediaAuthenticationAnswer();
    assertEquals("The 'P' bit is not copied from request in Multimedia-Authentication-Answer, it should. [RFC3588/6.2]", mar.getHeader().isProxiable(), maa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) mar).getGenericData().setProxiable(!mar.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Multimedia-Authentication-Request, it should.", mar.getHeader().isProxiable() != maa.getHeader().isProxiable());
    serverSession.fetchSessionData(mar);

    maa = serverSession.createMultimediaAuthenticationAnswer();
    assertEquals("The 'P' bit is not copied from request in Multimedia-Authentication-Answer, it should. [RFC3588/6.2]", mar.getHeader().isProxiable(), maa.getHeader().isProxiable());
  }

  @Test
  public void isRequestPPR() throws Exception {
    PushProfileRequest ppr = cxdxMessageFactory.createPushProfileRequest();
    assertTrue("Request Flag in Push-Profile-Request is not set.", ppr.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersPPR() throws Exception {
    PushProfileRequest ppr = cxdxMessageFactory.createPushProfileRequest();

    int nFailures = AvpAssistant.testMethods(ppr, PushProfileRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isRequestRTR() throws Exception {
    RegistrationTerminationRequest rtr = cxdxMessageFactory.createRegistrationTerminationRequest();
    assertTrue("Request Flag in Registration-Termination-Request is not set.", rtr.getHeader().isRequest());
  }

  @Test
  public void isProxiableRTR() throws Exception {
    RegistrationTerminationRequest rtr = cxdxMessageFactory.createRegistrationTerminationRequest();
    assertTrue("The 'P' bit is not set by default in Registration-Termination-Request, it should.", rtr.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersRTR() throws Exception {
    RegistrationTerminationRequest rtr = cxdxMessageFactory.createRegistrationTerminationRequest();

    int nFailures = AvpAssistant.testMethods(rtr, RegistrationTerminationRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isAnswerRTA() throws Exception {
    clientSession.fetchSessionData(cxdxMessageFactory.createRegistrationTerminationRequest());
    RegistrationTerminationAnswer rta = clientSession.createRegistrationTerminationAnswer();
    
    assertFalse("Request Flag in Registration-Termination-Answer is set.", rta.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersRTA() throws Exception {
    clientSession.fetchSessionData(cxdxMessageFactory.createRegistrationTerminationRequest());
    RegistrationTerminationAnswer rta = clientSession.createRegistrationTerminationAnswer();
    
    int nFailures = AvpAssistant.testMethods(rta, RegistrationTerminationAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostRTA() throws Exception {
    clientSession.fetchSessionData(cxdxMessageFactory.createRegistrationTerminationRequest());
    RegistrationTerminationAnswer rta = clientSession.createRegistrationTerminationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", rta.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmRTA() throws Exception {
    clientSession.fetchSessionData(cxdxMessageFactory.createRegistrationTerminationRequest());
    RegistrationTerminationAnswer rta = clientSession.createRegistrationTerminationAnswer();
    
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", rta.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedRTA() throws Exception {
    RegistrationTerminationRequest rtr = cxdxMessageFactory.createRegistrationTerminationRequest();
    clientSession.fetchSessionData(rtr);
    RegistrationTerminationAnswer rta = clientSession.createRegistrationTerminationAnswer();
    assertEquals("The 'P' bit is not copied from request in Registration-Termination-Answer, it should. [RFC3588/6.2]", rtr.getHeader().isProxiable(), rta.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) rtr).getGenericData().setProxiable(!rtr.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Registration-Termination-Request, it should.", rtr.getHeader().isProxiable() != rta.getHeader().isProxiable());
    clientSession.fetchSessionData(rtr);

    rta = clientSession.createRegistrationTerminationAnswer();
    assertEquals("The 'P' bit is not copied from request in Registration-Termination-Answer, it should. [RFC3588/6.2]", rtr.getHeader().isProxiable(), rta.getHeader().isProxiable());
  }

  @Test
  public void isRequestSAR() throws Exception {
    ServerAssignmentRequest sar = cxdxMessageFactory.createServerAssignmentRequest();
    assertTrue("Request Flag in Server-Assignment-Request is not set.", sar.getHeader().isRequest());
  }

  @Test
  public void isProxiableSAR() throws Exception {
    ServerAssignmentRequest sar = cxdxMessageFactory.createServerAssignmentRequest();
    assertTrue("The 'P' bit is not set by default in Server-Assignment-Request, it should.", sar.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersSAR() throws Exception {
    ServerAssignmentRequest sar = cxdxMessageFactory.createServerAssignmentRequest();

    int nFailures = AvpAssistant.testMethods(sar, ServerAssignmentRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isAnswerSAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createServerAssignmentRequest());
    ServerAssignmentAnswer saa = serverSession.createServerAssignmentAnswer();
    
    assertFalse("Request Flag in Server-Assignment-Answer is set.", saa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersSAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createServerAssignmentRequest());
    ServerAssignmentAnswer saa = serverSession.createServerAssignmentAnswer();
    
    int nFailures = AvpAssistant.testMethods(saa, ServerAssignmentAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostSAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createServerAssignmentRequest());
    ServerAssignmentAnswer saa = serverSession.createServerAssignmentAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", saa.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmSAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createServerAssignmentRequest());
    ServerAssignmentAnswer saa = serverSession.createServerAssignmentAnswer();
    
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", saa.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedSAA() throws Exception {
    ServerAssignmentRequest sar = cxdxMessageFactory.createServerAssignmentRequest();
    serverSession.fetchSessionData(sar);
    ServerAssignmentAnswer saa = serverSession.createServerAssignmentAnswer();
    assertEquals("The 'P' bit is not copied from request in Server-Assignment-Answer, it should. [RFC3588/6.2]", sar.getHeader().isProxiable(), saa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) sar).getGenericData().setProxiable(!sar.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in Server-Assignment-Request, it should.", sar.getHeader().isProxiable() != saa.getHeader().isProxiable());
    serverSession.fetchSessionData(sar);

    saa = serverSession.createServerAssignmentAnswer();
    assertEquals("The 'P' bit is not copied from request in Server-Assignment-Answer, it should. [RFC3588/6.2]", sar.getHeader().isProxiable(), saa.getHeader().isProxiable());
  }

  @Test
  public void isRequestUAR() throws Exception {
    UserAuthorizationRequest uar = cxdxMessageFactory.createUserAuthorizationRequest();
    assertTrue("Request Flag in User-Authorization-Request is not set.", uar.getHeader().isRequest());
  }

  @Test
  public void isProxiableUAR() throws Exception {
    UserAuthorizationRequest uar = cxdxMessageFactory.createUserAuthorizationRequest();
    assertTrue("The 'P' bit is not set by default in User-Authorization-Request, it should.", uar.getHeader().isProxiable());
  }

  @Test
  public void testGettersAndSettersUAR() throws Exception {
    UserAuthorizationRequest uar = cxdxMessageFactory.createUserAuthorizationRequest();

    int nFailures = AvpAssistant.testMethods(uar, UserAuthorizationRequest.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void isAnswerUAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createUserAuthorizationRequest());
    UserAuthorizationAnswer uaa = serverSession.createUserAuthorizationAnswer();
    
    assertFalse("Request Flag in Server-Assignment-Answer is set.", uaa.getHeader().isRequest());
  }

  @Test
  public void testGettersAndSettersUAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createUserAuthorizationRequest());
    UserAuthorizationAnswer uaa = serverSession.createUserAuthorizationAnswer();
    
    int nFailures = AvpAssistant.testMethods(uaa, UserAuthorizationAnswer.class);

    assertEquals("Some methods have failed. See logs for more details.", 0, nFailures);
  }

  @Test
  public void hasDestinationHostUAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createUserAuthorizationRequest());
    UserAuthorizationAnswer uaa = serverSession.createUserAuthorizationAnswer();

    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", uaa.getDestinationHost());
  }

  @Test
  public void hasDestinationRealmUAA() throws Exception {
    serverSession.fetchSessionData(cxdxMessageFactory.createUserAuthorizationRequest());
    UserAuthorizationAnswer uaa = serverSession.createUserAuthorizationAnswer();
    
    assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", uaa.getDestinationRealm());
  }

  @Test
  public void isProxiableCopiedUAA() throws Exception {
    UserAuthorizationRequest uar = cxdxMessageFactory.createUserAuthorizationRequest();
    serverSession.fetchSessionData(uar);
    UserAuthorizationAnswer uaa = serverSession.createUserAuthorizationAnswer();
    assertEquals("The 'P' bit is not copied from request in User-Authorization-Answer, it should. [RFC3588/6.2]", uar.getHeader().isProxiable(), uaa.getHeader().isProxiable());

    // Reverse 'P' bit ...
    ((DiameterMessageImpl) uar).getGenericData().setProxiable(!uar.getHeader().isProxiable());
    assertTrue("The 'P' bit was not modified in User-Authorization-Request, it should.", uar.getHeader().isProxiable() != uaa.getHeader().isProxiable());
    serverSession.fetchSessionData(uar);

    uaa = serverSession.createUserAuthorizationAnswer();
    assertEquals("The 'P' bit is not copied from request in User-Authorization-Answer, it should. [RFC3588/6.2]", uar.getHeader().isProxiable(), uaa.getHeader().isProxiable());
  }

  @Test
  public void testAvpFactoryCreateAssociatedIdentities() throws Exception {
    String avpName = "Associated-Identities";

    // Create AVP with mandatory values
    AssociatedIdentities aiAvp1 = cxdxAvpFactory.createAssociatedIdentities();

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", aiAvp1);

    // Create AVP with default constructor
    AssociatedIdentities aiAvp2 = cxdxAvpFactory.createAssociatedIdentities();

    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", aiAvp1, aiAvp2);

    // Make new copy
    aiAvp2 = cxdxAvpFactory.createAssociatedIdentities();

    // And set all values using setters
    AvpAssistant.testSetters(aiAvp2);

    // Create empty...
    AssociatedIdentities aiAvp3 = cxdxAvpFactory.createAssociatedIdentities();

    // Verify that no values have been set
    AvpAssistant.testHassers(aiAvp3, false);

    // Set all previous values
    aiAvp3.setExtensionAvps(aiAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(aiAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(aiAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", aiAvp2, aiAvp3);
  }

  @Test
  public void testAvpFactoryCreateAssociatedRegisteredIdentities() throws Exception {
    String avpName = "Associated-Registered-Identities";

    // Create AVP with mandatory values
    AssociatedRegisteredIdentities ariAvp1 = cxdxAvpFactory.createAssociatedRegisteredIdentities();

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", ariAvp1);

    // Create AVP with default constructor
    AssociatedRegisteredIdentities ariAvp2 = cxdxAvpFactory.createAssociatedRegisteredIdentities();

    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", ariAvp1, ariAvp2);

    // Make new copy
    ariAvp2 = cxdxAvpFactory.createAssociatedRegisteredIdentities();

    // And set all values using setters
    AvpAssistant.testSetters(ariAvp2);

    // Create empty...
    AssociatedRegisteredIdentities ariAvp3 = cxdxAvpFactory.createAssociatedRegisteredIdentities();

    // Verify that no values have been set
    AvpAssistant.testHassers(ariAvp3, false);

    // Set all previous values
    ariAvp3.setExtensionAvps(ariAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(ariAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(ariAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", ariAvp2, ariAvp3);
  }

  @Test
  public void testAvpFactoryCreateChargingInformation() throws Exception {
    String avpName = "Charging-Information";

    // Create AVP with mandatory values
    ChargingInformation ciAvp1 = cxdxAvpFactory.createChargingInformation();

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", ciAvp1);

    // Create AVP with default constructor
    ChargingInformation ciAvp2 = cxdxAvpFactory.createChargingInformation();

    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", ciAvp1, ciAvp2);

    // Make new copy
    ciAvp2 = cxdxAvpFactory.createChargingInformation();

    // And set all values using setters
    AvpAssistant.testSetters(ciAvp2);

    // Create empty...
    ChargingInformation ciAvp3 = cxdxAvpFactory.createChargingInformation();

    // Verify that no values have been set
    AvpAssistant.testHassers(ciAvp3, false);

    // Set all previous values
    ciAvp3.setExtensionAvps(ciAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(ciAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(ciAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", ciAvp2, ciAvp3);
  }

  @Test
  public void testAvpFactoryCreateDeregistrationReason() throws Exception {
    String avpName = "Deregistration-Reason";

    // Create AVP with mandatory values
    DeregistrationReason drAvp1 = cxdxAvpFactory.createDeregistrationReason(ReasonCode.NEW_SERVER_ASSIGNED);

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", drAvp1);

    // Create AVP with default constructor
    DeregistrationReason drAvp2 = cxdxAvpFactory.createDeregistrationReason();

    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Reason-Code AVP.", drAvp2.hasReasonCode());

    // Set mandatory values
    drAvp2.setReasonCode(ReasonCode.NEW_SERVER_ASSIGNED);

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", drAvp1, drAvp2);

    // Make new copy
    drAvp2 = cxdxAvpFactory.createDeregistrationReason();

    // And set all values using setters
    AvpAssistant.testSetters(drAvp2);

    // Create empty...
    DeregistrationReason drAvp3 = cxdxAvpFactory.createDeregistrationReason();

    // Verify that no values have been set
    AvpAssistant.testHassers(drAvp3, false);

    // Set all previous values
    drAvp3.setExtensionAvps(drAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(drAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(drAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", drAvp2, drAvp3);
  }

  @Test
  public void testAvpFactoryCreateRestorationInfo() throws Exception {
    String avpName = "Restoration-Info";

    // Create AVP with mandatory values
    RestorationInfo riAvp1 = cxdxAvpFactory.createRestorationInfo("SAMPLE.PATH".getBytes(), "SAMPLE.CONTACT".getBytes());

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", riAvp1);

    // Create AVP with default constructor
    RestorationInfo riAvp2 = cxdxAvpFactory.createRestorationInfo();

    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Path AVP.", riAvp2.hasPath());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Contact AVP.", riAvp2.hasContact());

    // Set mandatory values
    riAvp2.setPath("SAMPLE.PATH".getBytes());
    riAvp2.setContact("SAMPLE.CONTACT".getBytes());

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", riAvp1, riAvp2);

    // Make new copy
    riAvp2 = cxdxAvpFactory.createRestorationInfo();

    // And set all values using setters
    AvpAssistant.testSetters(riAvp2);

    // Create empty...
    RestorationInfo riAvp3 = cxdxAvpFactory.createRestorationInfo();

    // Verify that no values have been set
    AvpAssistant.testHassers(riAvp3, false);

    // Set all previous values
    riAvp3.setExtensionAvps(riAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(riAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(riAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", riAvp2, riAvp3);
  }

  @Test
  public void testAvpFactoryCreateSCSCFRestorationInfo() throws Exception {
    String avpName = "SCSCF-Restoration-Info";

    // Create AVP with mandatory values
    SCSCFRestorationInfo scscfriAvp1 = cxdxAvpFactory.createSCSCFRestorationInfo("SAMPLE.USERNAME", new RestorationInfo[] { cxdxAvpFactory.createRestorationInfo("SAMPLE.PATH".getBytes(), "SAMPLE.CONTACT".getBytes()) });

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", scscfriAvp1);

    // Create AVP with default constructor
    SCSCFRestorationInfo scscfriAvp2 = cxdxAvpFactory.createSCSCFRestorationInfo();

    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have User-Name AVP.", scscfriAvp2.hasUserName());
    Assert.assertTrue("Created " + avpName + " AVP from default constructor should not have Restoration-Info AVP.", scscfriAvp2.getRestorationInfos() == null
        || scscfriAvp2.getRestorationInfos().length == 0);

    // Set mandatory values
    scscfriAvp2.setUserName("SAMPLE.USERNAME");
    scscfriAvp2.setRestorationInfo(cxdxAvpFactory.createRestorationInfo("SAMPLE.PATH".getBytes(), "SAMPLE.CONTACT".getBytes()));

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", scscfriAvp1, scscfriAvp2);

    // Make new copy
    scscfriAvp2 = cxdxAvpFactory.createSCSCFRestorationInfo();

    // And set all values using setters
    AvpAssistant.testSetters(scscfriAvp2);

    // Create empty...
    SCSCFRestorationInfo scscfriAvp3 = cxdxAvpFactory.createSCSCFRestorationInfo();

    // Verify that no values have been set
    AvpAssistant.testHassers(scscfriAvp3, false);

    // Set all previous values
    scscfriAvp3.setExtensionAvps(scscfriAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(scscfriAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(scscfriAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", scscfriAvp2, scscfriAvp3);
  }

  @Test
  public void testAvpFactoryCreateSIPAuthDataItem() throws Exception {
    String avpName = "SIP-Auth-Data-Item";

    // Create AVP with mandatory values
    SIPAuthDataItem sadiAvp1 = cxdxAvpFactory.createSIPAuthDataItem();

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", sadiAvp1);

    // Create AVP with default constructor
    SIPAuthDataItem sadiAvp2 = cxdxAvpFactory.createSIPAuthDataItem();

    // Should not contain mandatory values

    // Set mandatory values

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", sadiAvp1, sadiAvp2);

    // Make new copy
    sadiAvp2 = cxdxAvpFactory.createSIPAuthDataItem();

    // And set all values using setters
    AvpAssistant.testSetters(sadiAvp2);

    // Create empty...
    SIPAuthDataItem sadiAvp3 = cxdxAvpFactory.createSIPAuthDataItem();

    // Verify that no values have been set
    AvpAssistant.testHassers(sadiAvp3, false);

    // Set all previous values
    sadiAvp3.setExtensionAvps(sadiAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(sadiAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(sadiAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", sadiAvp2, sadiAvp3);
  }

  @Test
  public void testAvpFactoryCreateSubscriptionInfo() throws Exception {
    String avpName = "Subscription-Info";

    // Create AVP with mandatory values
    SubscriptionInfo siAvp1 = cxdxAvpFactory.createSubscriptionInfo("cid".getBytes(), "fsh".getBytes(), "tsh".getBytes(), "rr".getBytes(), "c".getBytes());

    // Make sure it's not null
    Assert.assertNotNull("Created " + avpName + " AVP from objects should not be null.", siAvp1);

    // Create AVP with default constructor
    SubscriptionInfo siAvp2 = cxdxAvpFactory.createSubscriptionInfo();

    // Should not contain mandatory values
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Call-ID-SIP-Header AVP.", siAvp2.hasCallIDSIPHeader());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have From-SIP-Header AVP.", siAvp2.hasFromSIPHeader());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have To-SIP-Header AVP.", siAvp2.hasToSIPHeader());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Record-Route AVP.", siAvp2.hasRecordRoute());
    Assert.assertFalse("Created " + avpName + " AVP from default constructor should not have Contact AVP.", siAvp2.hasContact());

    // Set mandatory values
    siAvp2.setCallIDSIPHeader("cid".getBytes());
    siAvp2.setFromSIPHeader("fsh".getBytes());
    siAvp2.setToSIPHeader("tsh".getBytes());
    siAvp2.setRecordRoute("rr".getBytes());
    siAvp2.setContact("c".getBytes());

    // Make sure it's equal to the one created with mandatory values constructor
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + set<Mandatory-AVPs> should be equal to original.", siAvp1, siAvp2);

    // Make new copy
    siAvp2 = cxdxAvpFactory.createSubscriptionInfo();

    // And set all values using setters
    AvpAssistant.testSetters(siAvp2);

    // Create empty...
    SubscriptionInfo siAvp3 = cxdxAvpFactory.createSubscriptionInfo();

    // Verify that no values have been set
    AvpAssistant.testHassers(siAvp3, false);

    // Set all previous values
    siAvp3.setExtensionAvps(siAvp2.getExtensionAvps());

    // Verify if values have been set
    AvpAssistant.testHassers(siAvp3, true);

    // Verify if values have been correctly set
    AvpAssistant.testGetters(siAvp3);

    // Make sure they match!
    Assert.assertEquals("Created " + avpName + " AVP from default constructor + setUnitValue should be equal to original.", siAvp2, siAvp3);
  }

}
