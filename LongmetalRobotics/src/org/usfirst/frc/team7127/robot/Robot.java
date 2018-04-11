/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7127.robot;

// hi
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
	private Joystick armGamepad;	// Gamepad
	//private Joystick backupGamepad;	// Backup Gamepad
	private Timer robotTimer;	// Timer
	private DoubleSolenoid gripperSolenoid;	// Gripper pneumatic solenoid
	private Solenoid armSolenoid;	// Raising arm solenoid
	private DigitalInput topLimitSwitch;	// top limit switch
	private DigitalInput bottomLimitSwitch;	// bottom limit switch
	private DigitalInput jumpA;	// Position jumper a (position 2)
	private DigitalInput jumpB;	// Position jumper b (position 3)
	private DigitalInput jumpC;	// Position jumper c (position 4)
	private DigitalInput jumpD;	// Position jumper d (position 5)

	// Create variables to store joystick and limit switches values
	// We map all the Joystick Buttons, and then map buttons on the gamepad to buttons on the joystick.
	boolean button1Pressed = false; // Button 1, failsafe false
	boolean button2Pressed = false; // Button 2, failsafe false
	boolean button3Pressed = false;	// Button 3, failsafe false
	boolean button4Pressed = false;	// Button 4, failsafe false
	boolean button5Pressed = false;	// Button 5, failsafe false
	boolean button6Pressed = false;	// Button 6, failsafe false
	boolean button7Pressed = false; // Button 7, failsafe false
	boolean button8Pressed = false; // Button 8, failsafe false
	boolean button9Pressed = false; // Button 9, failsafe false
	boolean button10Pressed = false; // Button 10, failsafe false
	boolean button11Pressed = false; // Button 11, failsafe false
	boolean button12Pressed = false; // Button 12, failsafe false

	//Unsure if these buttons are used in our current configuration.
	boolean RB = false;
	boolean LB = false;

	// Mapping Limit Switches and Jumper Positions to failsafes.
	boolean limitSwitchTop = true;	// Top limit switch, failsafe true
	boolean limitSwitchBot = true;	// Bottom limit switch, failsafe true
	boolean jumperA = false;	// Position jumper A (socket 2), failsafe false
	boolean jumperB = false;	// Position jumper B (socket 3), failsafe false
	boolean improvedCode = false;	//Position Jumper C (socket 4), failsafe false
	boolean speedTestCode = false;	//Position Jumper D (socket 5), failsafe false

	// Code for driving speed and turning configuration.
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

		driveTrain = new DifferentialDrive(m_left, m_right);	// Create drivetrain with Spark groups


		verticalArm = new TalonSRX(0);	// Initialize vertical arm motor
		gripperL = new TalonSRX(1);		//  Initialize TalonMotor Controllers for intake wheels.
		gripperR = new TalonSRX(2);		//  Initialize TalonMotor Controllers for intake wheels.

		driveStick = new Joystick(0);	// Initialize joystick
		armGamepad = new Joystick(1);	// Initialize gamepad
		//backupGamepad = new Joystick(2);	// Initialize backup gamepad
		
		robotTimer = new Timer();	// Initialize timer

		gripperSolenoid = new DoubleSolenoid(0,1);	// Initialize gripper solenoids
		armSolenoid = new Solenoid(2);	// Initialize arm raising solenoid
		
		topLimitSwitch = new DigitalInput(0);	// Initialize top limit switch
		bottomLimitSwitch = new DigitalInput(1);	// Initialize bottom limit switch
		jumpA = new DigitalInput(2);	// Jumpers:
		jumpB = new DigitalInput(3);	// Failsafe: XX		1: A	2: AB	3: B
		jumpC = new DigitalInput(4);	// Configuring this jumper to allow for 'better autonomous period code attempts'
		jumpD = new DigitalInput(5);	// Configuring this jumper to allow for 'speed test run and turning test run attempts'

		CameraServer.getInstance().startAutomaticCapture();	// Start camera stream to DS
	}
	//
	@Override
	public void autonomousInit() {	// Initial autonomous code (run once at beginning of autonomous)
		gameData = DriverStation.getInstance().getGameSpecificMessage();	// Get game data (switch/scale positions)
		jumperA = !jumpA.get();	// Get Value of Position jumpers
		jumperB = !jumpB.get(); // Get Value of Position jumpers
		improvedCode = !jumpC.get(); 	// Get Value of Improved Auto jumper
		speedTestCode = !jumpD.get(); 	// Get Value of Speed Test jumper


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

		// Set timer to 0 and start it
		robotTimer.reset();	
		robotTimer.start();	
	}


	@Override
	public void autonomousPeriodic() {	// Period of Autonomous Code Instruction Set
		limitSwitchBot = bottomLimitSwitch.get();	// Get value of bottom limit switch
		limitSwitchTop = topLimitSwitch.get(); 		// Get value of top limit switch
	
		if(robotTimer.get()>0 && robotTimer.get()<1)
		{
			if(limitSwitchBot)
			{
				verticalArm.set(ControlMode.PercentOutput, -.9);  // LOWER THE ARM
			} else {verticalArm.set(ControlMode.PercentOutput, 0);}
		}
		if (robotTimer.get() > 1.0 && robotTimer.get() < 1.4) {	// Deploy arm for 0.4 seconds
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
    			case 1:	// Left Switch ...and we are to the left...
				if (robotTimer.get() > 1.3 && robotTimer.get() < 4.4) {	// ...timer 3.1 second(s)...
					driveTrain.arcadeDrive(0.7, 0.0);	// !!! drive straight at 0.7 speed
				} else if (robotTimer.get() > 4.4 && robotTimer.get() < 6.2) {	// ...timer 1.8 seconds...
					driveTrain.arcadeDrive(0.0, 0.6);	// !!! turn slowly right at 0.6 speed
				} else if (robotTimer.get() > 6.2 && robotTimer.get() < 7.2) {  // ...timer 1.0 seconds... 
					driveTrain.arcadeDrive(0.6, 0.0);   // !!! drive straight at 0.6 speed
				} else if (robotTimer.get() > 7.2 && robotTimer.get() < 7.3) {	// ...timer 0.1 seconds...
						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// !!! drop the cube
				} else {	// ...and is none of the above (more than 7.3 seconds and not improved code)...
					driveTrain.arcadeDrive(0.0, 0.0);	// !!! stop
				} else {	// IMPROVED CODE ATTEMPT
					if(gameData.charAt(1) == 'R' || improvedCode){
					if(robotTimer.get()>7.3 && robotTimer.get()<8.0) { // ...timer 0.7 seconds...
						driveTrain.arcadeDrive(-.6,0);	// !!! reverse at 0.6 speed
					} else if (robotTimer.get()>8.0 && robotTimer.get()<8.9) { // ... timer 0.9 seconds... 
						if (limitSwitchBot) {
								verticalArm.set(ControlMode.PercentOutput, -0.9);
						} else {
							verticalArm.set(ControlMode.PercentOutput, 0);
						}		// !!! LOWER THE ARM         AND
						driveTrain.arcadeDrive(0, -.8); // !!! Turn LEFT at 0.8 speed
					} else if (robotTimer.get()>8.9 && robotTimer.get()<9.6) { // ... timer 0.9 seconds...
						driveTrain.arcadeDrive(.7, 0);	// !!! Drive FORWARD 0.8 speed
					} else if (robotTimer.get()>9.6 && robotTimer.get()<10.5) { // ... timer 0.9 seconds...
						driveTrain.arcadeDrive(0, .8); // !!! Turn RIGHT at 0.8 speed
					} else if (robotTimer.get()>10.5 && robotTimer.get()<10.8) { // ... timer 0.3 seconds...
						driveTrain.arcadeDrive(.7, 0); // !!! Drive FORWARD at 0.7 speed
					} else if(robotTimer.get()>10.8 && robotTimer.get()<11.7) { // ... timer 0.9 seconds...
						driveTrain.arcadeDrive(0, .8); // !!! Turn RIGHT at 0.8 spee
					} else if (robotTimer.get()>11.7 && robotTimer.get()<12.2) { // ... timer 0.5 seconds...
						driveTrain.arcadeDrive(.6, 0); // !!! Drive FORWARD at 0.6 speed
					} else if (robotTimer.get()>12.2 && robotTimer.get<12.6) { // ... timer 0.4 seconds...
						gripperL.set(ControlMode.PercentOutput, 1.0);
						gripperR.set(ControlMode.PercentOutput, -1.0); // Run the INTAKE WHEELS
					} else if (robotTimer.get()>12.5 && robotTimer.get()<12.6) { // ... timer 0.1 seconds...
						gripperSolenoid.set(DoubleSolenoid.Value.kReverse); // CLOSE THE GRIPPER
					} else if (robotTimer.get()>12.6 && robotTimer.get()<13.2) { // ... timer 0.9 seconds...
						if(limitSwitchTop) {
							verticalArm.set(ControlMode.PercentOutput, .9);	}
						else {
							verticalArm.set(ControlMode.PercentOutput, 0);
						}		// !!! RAISE THE ARM

					}else if(robotTimer.get()>13.2 && robotTimer.get()<13.8) { // ... timer 0.6 seconds...
						driveTrain.arcadeDrive(.6,0); // !!! Drive FORWARD at 0.6 speed
					}else if(robotTimer.get()>13.8 && robotTimer.get()<13.9) {// ... timer 0.1 seconds...
						gripperSolenoid.set(DoubleSolenoid.Value.kForward); // !!! DROP THE CUBE
					}
				}
					//*****************************************************
					else {	// ...and is none of the above (more than 13.9 seconds)...
						driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
						verticalArm.set(ControlMode.PercentOutput, 0);
					}
					else if(gameData.charAt(1) == 'L' || improvedCode){
				}
				

			break;


    			case 2:	// Left Switch  ...and we are in the middle...
				if (!improvedCode)	// Regular Safe Autonomous Code
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.0) {	// ... timer 2.7 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				} else {	// ...and is none of the above 
						driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
						
				else if (improvedCode)  // IMPROVED CODE ATTEMPT
					if(robotTimer.get()>2 && robotTimer.get()<3.7) { // ... timer 1.7 seconds...
						driveTrain.arcadeDrive(0.7, 0);  // !!! FORWARD 0.7 speed
					} else if (robotTimer.get()>3.7 && robotTimer.get()<5.5) { // ... timer 1.8 seconds...
						driveTrain.arcadeDrive(0, -0.6);  // !!! ROTATE LEFT 0.6 (presumed 90deg)
					} else if (robotTimer.get()>5.5 && robotTimer.get()<6.6) { // ... timer 1.1 seconds...
						driveTrain.arcadeDrive(0.7, 0);  // !!! FORWARD 0.7 speed
					} else if (robotTime.get()>6.6 && robotTimer.get()<7.5) { // ... timer 2.7 seconds...
						driveTrain.arcadeDrive(0, 0.6);  // !!! ROTATE RIGHT 0.6 (presumed 90deg)
					} else if (robotTimer.get()>7.5 && robotTimer.get()<8.7) { // ... timer 1.2 seconds...
						driveTrain.arcadeDrive(0.7, 0); // !!! FORWARD 0.7 speed
					} else if (robotTimer.get()>9 && robotTimer.get()<9.1) { // ... timer 0.1 seconds...
						gripperSolenoid.set(DoubleSolenoid.Value.kFoward); // !!! DROP THE CUBE
					} else {	// ...and is none of the above...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    			break;
    			case 3:  // Left Switch ... and we are to the right...

    				if (robotTimer.get() > 1.3 && robotTimer.get() < 4.4) {	// ...timer 3.1 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
    				break;
			}
			

    	} else {	// If our switch is not to the left (to the right)...
    		switch (robotLocation) {
    			case 0:	// Right Switch ...and we do not know where we are...
    				driveTrain.arcadeDrive(0.0, 0.0);	// ... do not drive
    			break;
				case 1:	// Right Switch ...and we are to the left...
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.4) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				} /*else if (robotTimer.get() > 4.4 && robotTimer.get() < 6.2) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, 0.6);
    				}*/ else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
				case 2:	// Right Switch ...and we are in the middle...
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.4) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				/*} else if (robotTimer.get() > 1.2 && robotTimer.get() < 2.1) {	// ...and the time is between 1 and 1.4 seconds...
    					driveTrain.arcadeDrive(0.0, 0.6);	// ...correct the drift
    				} else if (robotTimer.get() > 2.1 && robotTimer.get() < 2.8) {	// ...and the time is between 1.4 and 4 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				} else if (robotTimer.get() > 2.8 && robotTimer.get() < 3.7) {	// ...and the time is between 1 and 1.4 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);	// ...correct the drift
    				} else if (robotTimer.get() > 3.7 && robotTimer.get() < 4.3) {	// ...and the time is between 1.4 and 4 seconds...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				*/} else if (robotTimer.get() > 5.8 && robotTimer.get() < 5.9) {
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
    					driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
    				}
				break;
				//
				case 3:	// Right Switch  ...and we are to the right...
					if (robotTimer.get() > 1.3 && robotTimer.get() < 4.4) {	// ...and the time is between 0.3 and 1 second(s)...
    					driveTrain.arcadeDrive(0.7, 0.0);	// ...drive straight at 0.7 speed
    				} else if (robotTimer.get() > 4.4 && robotTimer.get() < 6.2) {	// ...and the time is between 4 and 5.3 seconds...
    					driveTrain.arcadeDrive(0.0, -0.6);
    				} else if (robotTimer.get() > 6.2 && robotTimer.get() < 7.2) {
    					driveTrain.arcadeDrive(0.6, 0.0);
    				} else if (robotTimer.get() > 7.2 && robotTimer.get() < 7.3 ) {	// ... and the time is between 5.3 and 5.4 seconds...
    						gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...drop the cube
    				} else {	// ...and is none of the above (more than 5.4 seconds)...
							if(gameData.charAt(1) == 'L' || improvedCode){
							if(robotTimer.get()>6.1 && robotTimer.get()<6.8)
							{
								driveTrain.arcadeDrive(-.6,0);
							}else if(robotTimer.get()>6.8 && robotTimer.get()<7.7)
							{
								if(limitSwitchBot)
								{
										verticalArm.set(ControlMode.PercentOutput, -0.9);
								}else
								{
									verticalArm.set(ControlMode.PercentOutput, 0);
								}

								driveTrain.arcadeDrive(0, .8);
							}else if(robotTimer.get()>7.7 && robotTimer.get()<8.4)
							{
								driveTrain.arcadeDrive(.7,0);
							}else if(robotTimer.get()>8.4 && robotTimer.get()<9.3)
							{
								driveTrain.arcadeDrive(0, -.8);
							}else if(robotTimer.get()>9.3 && robotTimer.get()<9.6)
							{
								driveTrain.arcadeDrive(.7 , 0);
							}else if(robotTimer.get()>9.6 && robotTimer.get()<10.5)
							{
								driveTrain.arcadeDrive(0, -.8);
							}else if(robotTimer.get()>10.5 && robotTimer.get()<11)
							{
								driveTrain.arcadeDrive(.6, 0);
							}else if(robotTimer.get()>11 && robotTimer.get<11.4)
							{
								gripperL.set(ControlMode.PercentOutput, 1.0);
								gripperR.set(ControlMode.PercentOutput, -1.0);//in ?
							}else if(robotTimer.get()>11.3 && robotTimer.get()<11.4)
							{
								gripperSolenoid.set(DoubleSolenoid.Value.kReverse);
							}else if(robotTimer.get()>11.4 && robotTimer.get()<12)
							{
								if(limitSwitchTop)
								{
									verticalArm.set(ControlMode.PercentOutput, .9);
								}else
								{
									verticalArm.set(ControlMode.PercentOutput, 0);
								}

							}else if(robotTimer.get()>12 && robotTimer.get()<12.5)
							{
								driveTrain.arcadeDrive(.6,0);
							}else if(robotTimer.get()>12.5 && robotTimer.get()<12.6)
							{
								gripperSolenoid.set(DoubleSolenoid.Value.kForward);

							}
						}

							//*****************************************************
							else {	// ...and is none of the above (more than 5.4 seconds)...
								driveTrain.arcadeDrive(0.0, 0.0);	// ...stop
								verticalArm.set(ControlMode.PercentOutput, 0);
							}
						}
    				}
				break;
    		}
    	}

		System.out.println("Robot Position: " + robotLocation + "\tGame Data: " + gameData + "\tBottom Limit Switch Activated: " + limitSwitchBot + "\tTop Limit Switch Activated: " + limitSwitchTop + "\tVertical Arm Speed: " + verticalArm.getMotorOutputPercent() * 100 + "%");
    }

	@Override
	public void teleopPeriodic() {	// Periodic teleop code while teleop is active)
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
		 
		// LEGACY SPEED TESING CODE.. ********************************************************//
			// Findings were as follows:
				// 0.7 forward arcade drive = 17ft 2inches in 3 seconds.
				// 0.8 forward arcade drive = 16ft 2inches in 2 seconds.
				// 0.9 forward arcade drive = 21ft 1inch   in 2 seconds.
				// 1.0 forward arcade drive = 27ft 0inches in 2 seconds.
			// All of these findings were without 30lb arm and 15lb weights installed.

			/*if(EXAMPLE BUTTON PRESS TRIGGERS TEST START)
			{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=3)
					driveTrain.arcadeDrive(.7,0);
				driveTrain.arcadeDrive(0,0);
			}
			if(DIFFERENT BUTTON PRESS TRIGGERS SECOND TEST START)
				{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=2)
					driveTrain.arcadeDrive(.8,0);
				driveTrain.arcadeDrive(0,0);
			}*/
		//*******************************************************************************************//

		// NEW SPEED TESTING CODE.. ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//
		if(speedTestCode)
			if (button7Pressed)		// Test FORWARD AT 0.8 speed for 2 seconds.
			{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=2)
					driveTrain.arcadeDrive(.8,0);
				driveTrain.arcadeDrive(0,0);
				// UPDATE THE CODE WITH RESULTS HERE
			}
			if (button8Pressed)		// Test ROTATE SPEED at 0.8 for half a second.
				{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=0.5)
					driveTrain.arcadeDrive(0,0.8);
				driveTrain.arcadeDrive(0,0);
				// UPDATE THE CODE WITH RESULTS HERE
			}
			if (button9Pressed)		// Test FORWARD AT 0.9 speed for 2 seconds.
			{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=2)
					driveTrain.arcadeDrive(.9,0);
				driveTrain.arcadeDrive(0,0);
				// UPDATE THE CODE WITH RESULTS HERE
			}
			if (button10Pressed)		// Test NEGATIVE ROTATE SPEED at 0.8 for half a second.
				{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=0.5)
					driveTrain.arcadeDrive(0,-0.9);
				driveTrain.arcadeDrive(0,0);
				// UPDATE THE CODE WITH RESULTS HERE
			}
			if (button11Pressed)		// Test FORWARD AT 0.6 speed for 2 seconds.
			{
				robotTimer.reset();
				robotTimer.start();
				while(robotTimer.get()<=2)
					driveTrain.arcadeDrive(.6,0);
				driveTrain.arcadeDrive(0,0);
				// UPDATE THE CODE WITH RESULTS HERE
			}

	 	// Get the values of the buttons
	 	button1Pressed = driveStick.getRawButton(1);
	 	button2Pressed = driveStick.getRawButton(2);
		button3Pressed = driveStick.getRawButton(3);
		button4Pressed = driveStick.getRawButton(4);
		button5Pressed = driveStick.getRawButton(5);
		button6Pressed = driveStick.getRawButton(6);
		button7Pressed = driveStick.getRawButton(7);
		button8Pressed = driveStick.getRawButton(8);
		button9Pressed = driveStick.getRawButton(9);
		button10Pressed = driveStick.getRawButton(10);
		button11Pressed = driveStick.getRawButton(11);
		button12Pressed = driveStick.getRawButton(12);


		//if (armGamepad.getName() == "1 Controller (Gamepad F310)") {			// LEGACY COMMENTED CODE TO DIFFERENTIATE BETWEEN THE CONTROLLERS WE COULD HAVE PLUGGED IN.
			if (armGamepad.getPOV(0) == 180) {button3Pressed = true;}			// DPAD UP/DOWN
		 	if (armGamepad.getPOV(0) == 0) {button5Pressed = true;}				// DPAD UP/DOWN
		 	if (armGamepad.getRawButton(2) == true) {button4Pressed = true;}	// RED "B" BUTTON
		 	if (armGamepad.getRawButton(4) == true) {button6Pressed = true;}	// ORANGE "Y" BUTTON
		 	if (armGamepad.getRawButton(8) == true) {button12Pressed = true;}	// START Gamepad BUTTON
			if (armGamepad.getRawButton(6) == true) {button1Pressed = true;}	// RIGHT TOP SHOULDER
			if (armGamepad.getRawButton(5) == true) {button2Pressed = true;}	//  LEFT TOP SHOULDER
			if (armGamepad.getRawButton(8) == true) {button9Pressed = true;}	// if correct! RIGHT BOTTOM SHOULDER BUTTON  Perhaps this is Axis 3 0 to -1
			if (armGamepad.getRawButton(7) == true) {button8Pressed = true;}	// if correct!  LEFT BOTTOM SHOULDER BUTTON  Perhaps this is Axis 3 0 to 1
																				// if the above two items are an axis instead, take their input, invert the axis, and multiply by 0.5 to get intake/output speed.
																				// Or perhaps change the orientation of button 5 and 6 to be opposide, so you don't need to invert the axis.
			if (armGamepad.getRawButton(9) == true) {button11Pressed = true;}	// if correct! BACK Gamepad BUTTON  Perhaps this is "7" not "9"

		/*} else if (backupGamepad.getName() == "2 Controller (XBOX 360 For Windows)") { // LEGACY COMMENTED CODE TO DIFFERENTIATE BETWEEN THE CONTROLLERS WE COULD HAVE PLUGGED IN.
			if (backupGamepad.getPOV(0) == 180) {button3Pressed = true;}
		 	if (backupGamepad.getPOV(0) == 0) {button5Pressed = true;}
		 	if (backupGamepad.getRawButton(2) == true) {button4Pressed = true;}
		 	if (backupGamepad.getRawButton(4) == true) {button6Pressed = true;}
		 	if (backupGamepad.getRawButton(8) == true) {button12Pressed = true;}
		}*/

		// Get the values of the limit switches for the sled/lead screw position.
		limitSwitchTop = topLimitSwitch.get();
		limitSwitchBot = bottomLimitSwitch.get();

		// The following section of code fires the one way solenoid to release pressure into the main arm forward deployment system, only when required.
		//  NO NEW CODE WAS ENTERED HERE SINCE THE START OF the Fairfield EVENT.
		if (button12Pressed) {	// If button 12 is pressed...
			armSolenoid.set(true);	// ...raise the arm
		} else {	// If button 12 is not pressed...
			armSolenoid.set(false);	// ...do not raise the arm
		}

		// The following code section runs the Talon Motor Controllers for the intake wheels, in either an intake or output direction.
		// New code has been added here by NP on 4.11.2018 in order to provide our drivers with a slower and a faster input speed, as well as a stop wheels button.
		// A check is now performed to ensure that we're not speed testing code so that we can use the same buttons for different functionality based on jumper position.
		if(!speedTestCode) {
			if (button1Pressed && !button2Pressed) {
				gripperL.set(ControlMode.PercentOutput, 1.0);
				gripperR.set(ControlMode.PercentOutput, 1.0);
			} else if (!button1Pressed && button2Pressed) {
				gripperL.set(ControlMode.PercentOutput, -1.0);
				gripperR.set(ControlMode.PercentOutput, -1.0);
			} else if (button9Pressed && !button8Pressed) {
				gripperL.set(ControlMode.PercentOutput, 0.5);
				gripperR.set(ControlMode.PercentOutput, 0.5);
			} else if (!button9Pressed && button8Pressed) {
				gripperL.set(ControlMode.PercentOutput, -0.5);
				gripperR.set(ControlMode.PercentOutput, -0.5);
			} else if (button11Pressed) {
				gripperL.set(ControlMode.PercentOutput, 0.0);
				gripperR.set(ControlMode.PercentOutput, 0.0);
			} else {
			gripperL.set(ControlMode.PercentOutput, .1);
			gripperR.set(ControlMode.PercentOutput, .1);
			}
		}

		//  The following code section runs the Talon Motor Controller for the Sled / Lead Screw Motor...
		//  NO NEW CODE WAS ENTERED HERE SINCE WE SPED UP THE RAISE/LOWER SPEEDS on Sat/Sun of Fairfield EVENT.
		if (button5Pressed && !button3Pressed && limitSwitchTop) {	// If button 5 is pressed and top limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, 0.9);	// Move the arm up
		} else if (!button5Pressed && button3Pressed && limitSwitchBot) {	// If button 3 is pressed and bottom limit switch is not activated,
				verticalArm.set(ControlMode.PercentOutput, -0.9);	// Move the arm down
		} else {	// If nothing is activated or a limit switch is activated,
			verticalArm.set(ControlMode.PercentOutput, 0.0);	// Stop the arm
		}

		//  The following code section runs the DoubleSolenoid in one direction or the other, to open and close the gripper arms... 
		//  NO NEW CODE WAS ENTERED HERE SINCE the START of Fairfield EVENT.
		if (button6Pressed && !button4Pressed) {	// If button 6 but not button 4 is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kForward);	// ...open the gripper
		} else if (button4Pressed && !button6Pressed) {	// If button 4 but not button 6 is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kReverse);	// ...close the gripper
		} else {	// If either both or neither button is pressed...
			gripperSolenoid.set(DoubleSolenoid.Value.kOff);	// ...stop moving the gripper
		}
		// System.out.println("OrigPos: " + robotLocation + "\tButton 3 Pressed: " + button3Pressed + "\tButton 4 Pressed: " + button4Pressed + "\tButton 5 Pressed: " + button5Pressed + "\tButton 6 Pressed: " + button6Pressed + "\tButton 12 Pressed: " + button12Pressed + "\tBottom Limit Switch Activated: " + limitSwitchBot + "\tTop Limit Switch Activated: " + limitSwitchTop  + "\tVertical Arm Speed: " + verticalArm.getMotorOutputPercent() * 100 + "%");
	}
}