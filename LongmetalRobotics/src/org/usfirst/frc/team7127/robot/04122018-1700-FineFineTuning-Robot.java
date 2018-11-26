/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.usfirst.frc.team7127.robot;
//Import needed classes
import com.ctre.phoenix.motorcontrol.ControlMode;	// Import classes from CTRE
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;	// Import WPILib classes
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Robot extends IterativeRobot {
	// Create objects
	private DifferentialDrive driveTrain;	// Drive train
	private TalonSRX verticalArm;	// Vertical motor
	private TalonSRX gripperL;
	private TalonSRX gripperR;
	private Joystick driveStick;	// Joystick
	//tank drive joystick
	
	
	//
	private Joystick armGamepad;	// Gamepad
	//private Joystick backupGamepad;	// Backup Gamepad
	private Timer robotTimer;	// Timer
	private DoubleSolenoid gripperSolenoid;	// Gripper pneumatic solenoid
	private Solenoid armSolenoid;	// Raising arm solenoid
	private DigitalInput topLimitSwitch;	// top limit switch
	private DigitalInput bottomLimitSwitch;	// bottom limit switch
	private DigitalInput jumpA;	// Position jumper a
	private DigitalInput jumpB;	// Position jumper b
	
	// Create variables to store joystick and limit switches values
	boolean button1Pressed = false;
	boolean button2Pressed = false;
	boolean button3Pressed = false;	// Button 3, failsafe false
	boolean button4Pressed = false;	// Button 4, failsafe false
	boolean button5Pressed = false;	// Button 5, failsafe false
	boolean button6Pressed = false;	// Button 6, failsafe false
	boolean button12Pressed = false;	// Button 12, failsafe false
	boolean RB = false;
	boolean LB = false;
	boolean limitSwitchTop = true;	// Top limit switch, failsafe true
	boolean limitSwitchBot = true;	// Bottom limit switch, failsafe true
	boolean jumperA = false;	// Position jumper A, failsafe false
	boolean jumperB = false;	// Position jumper B, failsafe false
	
	// Test code for drive speed vs distance testing.
		boolean button11Pressed = false;
		boolean button10Pressed = false;
		boolean button9Pressed = false;
		boolean button8Pressed = false;
		
	double speed = 0.5;
	double driveY = 0.0;
	double driveX = 0.0;
	
	int robotLocation = 0;	// Robot location, failsafe 0
	
	String gameData;	// Game data, failsafe empty

	@Override
	public void robotInit() {
		Spark m_rearLeft = new Spark(0);	// Initialize left Spark objects
		Spark m_frontLeft = new Spark(1);
		
		SpeedControllerGroup m_left = new SpeedControllerGroup(m_rearLeft, m_frontLeft);	// Join left Sparks into a group
		
		Spark m_frontRight = new Spark(2);	// Initialize right Spark objects
		Spark m_rearRight = new Spark(3);
		SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);	// Join right Sparks into a group
		
		gripperL = new TalonSRX(1);
		gripperR = new TalonSRX(2);
		
		driveTrain = new DifferentialDrive(m_left, m_right);	// Create drivetrain with Spark groups
		verticalArm = new TalonSRX(0);	// Initialize vertical arm motor
		driveStick = new Joystick(0);	// Initialize joystick
		armGamepad = new Joystick(1);	// Initialize gamepad
		//backupGamepad = new Joystick(2);	// Initialize backup gamepad
		robotTimer = new Timer();	// Initialize timer
		gripperSolenoid = new DoubleSolenoid(0,1);	// Initialize gripper solenoids
		armSolenoid = new Solenoid(2);	// Initialize arm raising solenoid
		topLimitSwitch = new DigitalInput(0);	// Initialize top limit switch
		bottomLimitSwitch = new DigitalInput(1);	// Initialize bottom limit switch
		jumpA = new DigitalInput(2);	// Jumpers:
		jumpB = new DigitalInput(3);	// FS: XX	1: A	2: AB	3: B
		
		CameraServer.getInstance().startAutomaticCapture();	// Start camera stream to DS
	}
	//
	@Override
	public void autonomousInit() {	// Initial autonomous code (run once at beginning of autonomous)
		gameData = DriverStation.getInstance().getGameSpecificMessage();	// Get game data (switch/scale positions)
		jumperA = !jumpA.get();	// Get Value of Position jumpers
		jumperB = !jumpB.get();
		
		// Set robotLocation based on jumpers
		if (jumperA && !jumperB) {	// 2
			robotLocation = 1;
		} else if (jumperA && jumperB) {	// 2 & 3
			robotLocation = 2;
		} else if (!jumperA && jumperB) {	// 3
			robotLocation = 3;
		} else {	// None
			robotLocation = 0;	// Failsafe
		}
		
		robotTimer.reset();	// Set timer to 0
		robotTimer.start();	// and start it
	}
	
	
	@Override
	public void autonomousPeriodic() {	// Periodic autonomous code (run every (10ms?) while autonomous is active)
		limitSwitchBot = bottomLimitSwitch.get();	// Get value of bottom limit switch
		limitSwitchTop = topLimitSwitch.get();
		/*if (robotTimer.get() < 0.0 && limitSwitchBot) {	// Move arm down
			verticalArm.set(ControlMode.PercentOutput, -0.5);
		} else {
			verticalArm.set(ControlMode.PercentOutput, 0.0);
		}*/
		if(robotTimer.get()>0 && robotTimer.get()<1) 
		{
			if(limitSwitchBot) 
			{
				verticalArm.set(ControlMode.PercentOutput, -.9);
			} else {verticalArm.set(ControlMode.PercentOutput, 0);}
		}
		else if (robotTimer.get() > 1.0 && robotTimer.get() < 1.4) {	// Deploy arm for 0.4 seconds
			verticalArm.set(ControlMode.PercentOutput, 0);
			armSolenoid.set(true);
		} else {
			armSolenoid.set(false);
		}
		if(robotTimer.get()>1.4 && robotTimer.get()<2.4) 
		{
			if(limitSwitchTop) 
			{
				verticalArm.set(ControlMode.PercentOutput, .9);
			}
			else {verticalArm.set(ControlMode.PercentOutput, 0);}
		}
		if(gameData.length() > 0 &&  gameData.charAt(0) == 'L') {	// If our switch is to the left...
    		switch (robotLocation) {
    			case 0:	// ...and we do not know where we are...
    				driveTrain.arcadeDrive(0.0, 0.0);	// ...don't drive
    			break;
    			case 1:	// Left Switch ...and we are to the left... PONG DROP LEFT
    				if (robotTimer.get() > 1.3 && robotTimer.get() < 4.6) {	// ...timer 3.3 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ... FORWARD 0.7 speed
    				} else if (robotTimer.get() > 4.7 && robotTimer.get() < 6.6) {	// ...timer 1.9 seconds...
    					driveTrain.arcadeDrive(0.0, 0.6);	// ... ROTATE RIGHT 0.6 speed
    				} else if (robotTimer.get() > 6.6 && robotTimer.get() < 8.0) { // ... timer 1.4 seconds...
    					driveTrain.arcadeDrive(0.6, 0.0);	// ... FORWARD 0.7 speed
    				} else if (robotTimer.get() > 8.0 && robotTimer.get() < 8.1 ) {	// ... and the time is between 5.3 and 5.4 seconds...
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    			break;
    			case 2:	// Left Switch  ...and we are in the middle...  SONIC LEFT
    				if (robotTimer.get() > 1.3 && robotTimer.get() < 2.0) {	// ... timer 0.7 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// !!! FORWARD at 0.7 speed
    				} else if (robotTimer.get() > 2.0 && robotTimer.get() < 3.8) { // ... timer 1.8 seconds...
    					driveTrain.arcadeDrive(0, -0.6);  // !!! ROTATE LEFT 0.6 (presumed 90deg)
    				} else if (robotTimer.get() > 3.8 && robotTimer.get() < 6.5) { // ... timer 2.7 seconds...
    					driveTrain.arcadeDrive(0.7, 0);  // !!! FORWARD 0.7 speed (presumed 12ft)
    				} else if (robotTimer.get() > 6.5 && robotTimer.get() < 8.3) { // ... timer 1.8 seconds...
    					driveTrain.arcadeDrive(0, 0.6);  // !!! ROTATE RIGHT 0.6 (presumed 90deg)
    				} else if (robotTimer.get() > 8.3 && robotTimer.get() < 10.3) { // ... timer 2.0 seconds...
    					driveTrain.arcadeDrive(0.7, 0); // !!! FORWARD 0.7 speed (presumed 12ft total forward motion towards switch after this)
    				} else if (robotTimer.get() > 10.3 && robotTimer.get() < 10.4) { // ... timer 0.1 seconds...
    					gripperSolenoid.set(DoubleSolenoid.Value.kForward); // !!! DROP THE CUBE
    				} else {	// ...and is none of the above...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    			break;
    			case 3:  // Left Switch ... and we are to the right... DONKEY KONG LEFT
    				if (robotTimer.get() > 1.3 && robotTimer.get() < 4.5) {	// ...timer 3.2 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...FORWARD 0.7 speed
    				} else if (robotTimer.get() > 4.5 && robotTimer.get() < 6.4) {	// ...timer 1.9 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ...ROTATE LEFT (neg)0.6 
    				} else {	// ...and is none of the above...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    				break;
    		}
    	} else {	// If our switch is not to the left (to the right)...
    		switch (robotLocation) {
    			case 0:	// Right Switch ...and we do not know where we are...
    				driveTrain.arcadeDrive(0.0, 0.0);	// ... do not drive
    			break;
				case 1:	// Right Switch ...and we are to the left... DONKEY KONG RIGHT
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.5) {	// ...timer 3.2 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...FORWARD 0.7 speed
    				} else if (robotTimer.get() > 4.5 && robotTimer.get() < 6.4) {	// ...timer 1.9 seconds...
    					driveTrain.arcadeDrive(0.0, 0.6);	// ...ROTATE RIGHT 0.6 
    				} else {	// ...and is none of the above...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
				case 2:	// Right Switch ...and we are in the middle...  MARIO RIGHT
					if (robotTimer.get() > 1.3 && robotTimer.get() < 2.3) {	// ...timer 1.0 second...
						driveTrain.arcadeDrive(0.7, 0.0);	// !!! FORWARD at 0.7 speed
					} else if (robotTimer.get() > 2.3 && robotTimer.get() < 2.6) {	// ...timer 0.3 seconds...
						driveTrain.arcadeDrive(0.0, 0.6);   // !!! ROTATE slightly right
					} else if (robotTimer.get() > 2.6 && robotTimer.get() < 3.0) { // ... timer 0.4 seconds...
						driveTrain.arcadeDrive(0.7, 0.0);   // !!! FORWARD 0.7 speed
					} else if (robotTimer.get() > 3.0 && robotTimer.get() < 3.3) {	// ...timer 0.3 seconds...
						driveTrain.arcadeDrive(0.0, -0.6);   // !!! ROTATE left
					} else if (robotTimer.get() > 3.3 && robotTimer.get() < 4.7) { // ... timer 1.4 seconds...
						driveTrain.arcadeDrive(0.7, 0.0);   // !!! FORWARD 0.7 speed
					} else if (robotTimer.get() > 4.8 && robotTimer.get() < 4.9 ) {	// ... and the time is between 5.3 and 5.4 seconds...
						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// !!!drop the cube
					} else {	// ...and is none of the above (more than 5.4 seconds)...
						driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
					}
				break;
				//
				case 3:	// Right Switch  ...and we are to the right...  PONG DROP RIGHT
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.6) {	// ...timer 3.3 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ... FORWARD 0.7 speed
    				} else if (robotTimer.get() > 4.7 && robotTimer.get() < 6.6) {	// ...timer 1.9 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ... ROTATE LEFT (neg)0.6 speed
    				} else if (robotTimer.get() > 6.6 && robotTimer.get() < 8.0) { // ... timer 1.4 seconds...
    					driveTrain.arcadeDrive(0.6, 0.0);	// ... FORWARD 0.7 speed
    				} else if (robotTimer.get() > 8.0 && robotTimer.get() < 8.1 ) {	// ... and the time is between 5.3 and 5.4 seconds...
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
    		}
    	}
		
		//System.out.println("Robot Position: " + robotLocation + "\tGame Data: " + gameData + "\tBottom Limit Switch Activated: " + limitSwitchBot + "\tTop Limit Switch Activated: " + limitSwitchTop + "\tVertical Arm Speed: " + verticalArm.getMotorOutputPercent() * 100 + "%");
    }	
	
	@Override
	public void teleopPeriodic() {	// Periodic teleop code (run every (10ms?) while teleop is active)
		//if (driveStick.getName() == "0 Logitech Extreme 3D" ) {
			speed = driveStick.getRawAxis(3) + 1.1;	// Get value of joystick throttle
			driveY = -driveStick.getY() / speed;
			driveX = driveStick.getZ() / 1.5;
		/*} else if (armGamepad.getName() == "1 Controller (Gamepad F310)") {
			driveY = armGamepad.getRawAxis(1) / 0.5;
			driveX = armGamepad.getRawAxis(0) / 0.5;
		} else if (backupGamepad.getName() == "2 Controller (XBOX 360 For Windows)") {
			driveY = backupGamepad.getRawAxis(1) / 0.5;
			driveX = backupGamepad.getRawAxis(0) / 0.5;
		}*/
		
	 	driveTrain.arcadeDrive(driveY, driveX);	// Drive the robot
	 	//driveTrain.tankDrive(-driveStick.getY(), -driveStick2.getY());
	 	
	 	// Get the values of the buttons
	 	button1Pressed = driveStick.getRawButton(1);
	 	button2Pressed = driveStick.getRawButton(2);
		button3Pressed = driveStick.getRawButton(3);
		button4Pressed = driveStick.getRawButton(4);
		button5Pressed = driveStick.getRawButton(5);
		button6Pressed = driveStick.getRawButton(6);
		button12Pressed = driveStick.getRawButton(12);
	
		button11Pressed = driveStick.getRawButton(11);
		button10Pressed = driveStick.getRawButton(10);
		button9Pressed = driveStick.getRawButton(9);
		button8Pressed = driveStick.getRawButton(8);
		
		// test code********************************************************************************//
				/*if(button8Pressed)
				{
					robotTimer.reset();
					robotTimer.start();
					while(robotTimer.get()<=3)
						driveTrain.arcadeDrive(.7,0);
					driveTrain.arcadeDrive(0,0);
					//*  17ft 2in over 3 seconds...
				}
				if(button9Pressed)
				{
					robotTimer.reset();
					robotTimer.start();
					while(robotTimer.get()<=2)
						driveTrain.arcadeDrive(.8,0);
					driveTrain.arcadeDrive(0,0);
					//*  16ft 2in in 2 seconds...
				}
				if(button10Pressed)
				{
					robotTimer.reset();
					robotTimer.start();
					while(robotTimer.get()<=2)
						driveTrain.arcadeDrive(.9,0);
					driveTrain.arcadeDrive(0,0);
					//*  21ft 1in in 2 seconds
				}
				if(button11Pressed)
				{
					// 1 sec 6in
					robotTimer.reset();
					robotTimer.start();
					while(robotTimer.get()<=2)
						driveTrain.arcadeDrive(1,0);
					driveTrain.arcadeDrive(0,0);
					//* 27ft in 2 seconds
				}
				//
				/*
				 * 
				 * */
				//
				
				//*******************************************************************************************//
		
		//if (armGamepad.getName() == "1 Controller (Gamepad F310)") {
			if (armGamepad.getPOV(0) == 180) {button3Pressed = true;}
		 	if (armGamepad.getPOV(0) == 0) {button5Pressed = true;}
		 	if (armGamepad.getRawButton(2) == true) {button4Pressed = true;}
		 	if (armGamepad.getRawButton(4) == true) {button6Pressed = true;}
		 	if (armGamepad.getRawButton(8) == true) {button12Pressed = true;}
		 	if(armGamepad.getRawButton(6)==true) {button1Pressed =true;}
		 	if(armGamepad.getRawButton(5)== true) {button2Pressed = true;}
		/*} else if (backupGamepad.getName() == "2 Controller (XBOX 360 For Windows)") {
			if (backupGamepad.getPOV(0) == 180) {button3Pressed = true;}
		 	if (backupGamepad.getPOV(0) == 0) {button5Pressed = true;}
		 	if (backupGamepad.getRawButton(2) == true) {button4Pressed = true;}
		 	if (backupGamepad.getRawButton(4) == true) {button6Pressed = true;}
		 	if (backupGamepad.getRawButton(8) == true) {button12Pressed = true;}
		}*/
		
		// Get the values of the limit switches
		limitSwitchTop = topLimitSwitch.get();
		limitSwitchBot = bottomLimitSwitch.get();
		
		if (button12Pressed) {	// If button 12 is pressed...
			armSolenoid.set(true);	// ...raise the arm
		} else {	// If button 12 is not pressed...
			armSolenoid.set(false);	// ...do not raise the arm
		}
		
		if (button1Pressed && !button2Pressed) {
			gripperL.set(ControlMode.PercentOutput, 1.0);
			gripperR.set(ControlMode.PercentOutput, 1.0);
		} else if (!button1Pressed && button2Pressed) {
			gripperL.set(ControlMode.PercentOutput, -1.0);
			gripperR.set(ControlMode.PercentOutput, -1.0);
		} else {
			gripperL.set(ControlMode.PercentOutput, .1);
			gripperR.set(ControlMode.PercentOutput, .1);
		}
		
		if (button5Pressed && !button3Pressed && limitSwitchTop) {	// If button 5 is pressed and top limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, 0.9);	// Move the arm up
				
		} else if (!button5Pressed && button3Pressed && limitSwitchBot) {	// If button 3 is pressed and bottom limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, -0.9);	// Move the arm down
				
		} else {	// If nothing is activated or a limit switch is activated,
			verticalArm.set(ControlMode.PercentOutput, 0.0);	// Stop the arm
		} 
		
		if (button6Pressed && !button4Pressed) {	// If button 6 but not button 4 is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...open the gripper
		} else if (button4Pressed && !button6Pressed) {	// If button 4 but not button 6 is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kReverse);	// ...close the gripper
		} else {	// If either both or neither button is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kOff);	// ...stop moving the gripper
		}
		
		/*if(LB&&!RB) 
		{
			gripperL.set(ControlMode.PercentOutput, -1.0);
			gripperR.set(ControlMode.PercentOutput, -1.0);
			
		}else if(!LB&&RB) 
		{
			gripperL.set(ControlMode.PercentOutput, 1.0);
			gripperR.set(ControlMode.PercentOutput, 1.0);
		}
		else
		{
			gripperL.set(ControlMode.PercentOutput, 0);
			gripperR.set(ControlMode.PercentOutput, 0);
		}*/
		//System.out.println("OrigPos: " + robotLocation + "\tButton 3 Pressed: " + button3Pressed + "\tButton 4 Pressed: " + button4Pressed + "\tButton 5 Pressed: " + button5Pressed + "\tButton 6 Pressed: " + button6Pressed + "\tButton 12 Pressed: " + button12Pressed + "\tBottom Limit Switch Activated: " + limitSwitchBot + "\tTop Limit Switch Activated: " + limitSwitchTop  + "\tVertical Arm Speed: " + verticalArm.getMotorOutputPercent() * 100 + "%");
	}
}


