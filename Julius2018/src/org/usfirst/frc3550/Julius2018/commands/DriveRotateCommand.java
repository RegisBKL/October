package org.usfirst.frc3550.Julius2018.commands;

import org.usfirst.frc3550.Julius2018.Robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveRotateCommand extends Command {
    private double m_setpoint = 0;
	private static final double kP = 7; //
	// integral speed constant
	private static final double kI = 0.01; //0.018
	// derivative speed constant
	private static final double kD = 1; //1.5
	// derivative speed constant
	private static final double kF = 0;
	private double m_error = 0;
	double m_rotateValue;
	private PIDController m_GyroPID; 
	private PIDOutput m_PIDOutput;

	// Angle in degre
	// Direction = 0: Left 1:Right
	public DriveRotateCommand(double angle, int direction) {
		if(direction == 0){
		this.m_setpoint = -angle;
		}
		else if(direction == 1){
			this.m_setpoint = Math.abs(angle);
		}
		else this.m_setpoint = Math.abs(angle);
		// Use requires() here to declare subsystem dependencies
		requires(Robot.driveTrain);
		m_GyroPID = new PIDController(kP,kI,kD,kF, Robot.driveTrain.getAhrs(),  new MyPidOutput());
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		Robot.driveTrain.getAhrs().reset();
		//m_GyroPID.disable(); // begin PID control
		m_GyroPID.setInputRange(-360.0f, 360.0f);
		//m_GyroPID.setOutputRange(-0.4, 0.4);
		m_GyroPID.setAbsoluteTolerance(0.1f);
		// error = setpoint - Robot.drivetrain.getAhrs().getAngle();
		// Set setpoint of the pid controller
		SmartDashboard.putNumber("AngleSetpoint", m_setpoint);
		m_GyroPID.setContinuous(true);
		m_GyroPID.setSetpoint(m_setpoint);
		m_GyroPID.enable(); // begin PID control
		m_GyroPID.enable(); // begin PID control
       // setTimeout(1);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		// Set setpoint of the pid controller
		m_error = m_setpoint - Robot.driveTrain.getAhrs().getAngle();
		SmartDashboard.putNumber("actual_AngleError", m_error);
		SmartDashboard.putNumber("PIDinsideAngle", Robot.driveTrain.getAhrs().getAngle());
		SmartDashboard.putNumber("SetPoint_InsidePID",-m_rotateValue);
		//	m_GyroPID.setSetpoint(setpoint);
		//Robot.driveTrain.arcadeDrive(0, -rotateValue); // robot de competition
		Robot.driveTrain.arcadeDrive(0, m_rotateValue); // robot de test
		
		//m_GyroPID.enable(); // begin PID control	
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		//return (m_GyroPID.onTarget() || isTimedOut());
		return (Math.abs(m_error)<1);
		//return ((Math.abs(error)<1) || isTimedOut());
	}

	// Called once after isFinished returns true
	protected void end() {
		//Robot.drivetrain.disableDriveTrain();
		Robot.driveTrain.stop();
		m_GyroPID.disable(); // begin PID control
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}

	private class MyPidOutput implements PIDOutput {
		@Override
		public void pidWrite(double output) {
			m_rotateValue = output;
			if(m_rotateValue > 0.6) //0.5 robot de competition
				m_rotateValue = 0.6;
			else if(m_rotateValue < -0.6)
				m_rotateValue = -0.6;	
		}
	}
}
