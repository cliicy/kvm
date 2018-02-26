package com.arcserve.winrm;

import org.openwsman.Client;

import com.arcserve.winrm.Classes.ClassInstance;
import com.arcserve.winrm.Classes.ClassReference;

/**
 * @see http://msdn.microsoft.com/en-us/library/cc136808(v=vs.85).aspx
 * */
public class ConcreteJob {
    public enum State {
        /** The job has never been started. */
        New(2),
        /** The job is moving from the 'New', 'Suspended', or 'Service' states into the 'Running' state. */
        Starting(3),
        /** The job is running. */
        Running(4),
        /** The job is stopped, but can be restarted in a seamless manner. */
        Suspended(5),
        /** The job is moving to a 'Completed', 'Terminated', or 'Killed' state. */
        ShuttingDown(6),
        /** The job has completed normally. */
        Completed(7),
        /** The job has been stopped by a 'Terminate' state change request. The job and all its underlying processes are ended and can be restarted (this is job-specific) only as a new job. */
        Terminated(8),
        /** The job has been stopped by a 'Kill' state change request. Underlying processes might have been left running, and cleanup might be required to free up resources. */
        Killed(9),
        /** The job is in an abnormal state that might be indicative of an error condition. Actual status might be displayed though job-specific objects. */
        Exception(10),
        /** The job is in a vendor-specific state that supports problem discovery, or resolution, or both. */
        Service(11),
        
        Reserved(Integer.MAX_VALUE);
        
        int state;
        
        State(int initState) {
            this.state = initState;
        }
        
        public int getIntValue() {
            return this.state;
        }
        
        public static State getState(int state) {
            for (State s : State.values())
                if (state == s.getIntValue())
                    return s;
            return Reserved;
        }
    }
    
    private ClassReference jobRef = null;
    private ClassInstance  job    = null;
    
    public ConcreteJob(ClassReference job) {
        this.jobRef = job;
    }
    
    public String dump() {
        return this.job.dumpPropertiesForDebug();
    }
    
    public ConcreteJob refreshConcreteJob(Client conn) {
        this.job = this.jobRef.getInstance(conn);
        return this;
    }
    
    public Boolean isFinalized() throws Exception {
        return this.getJobState().getIntValue() > ConcreteJob.State.ShuttingDown.getIntValue();
    }
    
    public Boolean isSuccess() throws Exception {
        return this.getJobState() == ConcreteJob.State.Completed;
    }
    
    public State getJobState() throws Exception {
        if (this.job == null)
            throw new Exception("Call refreshConcreteJob first.");
        return State.getState(Integer.parseInt(job.getProperty("JobState").getSimpleValue()));
    }
    
    public String getStatusDescriptions() throws Exception {
        if (this.job == null)
            throw new Exception("Call refreshConcreteJob first.");
        String res = "";
        for (String line : job.getProperty("StatusDescriptions").getSimpleArrayValue())
            res += line + "\n";
        return res;
    }
    
    public String getErrorSummaryDescription() throws Exception {
        if (this.job == null)
            throw new Exception("Call refreshConcreteJob first.");
        return job.getProperty("ErrorSummaryDescription").getSimpleValue();
    }
    
    public String getErrorDescription() throws Exception {
        if (this.job == null)
            throw new Exception("Call refreshConcreteJob first.");
        String em = job.getProperty("ErrorDescription").getSimpleValue();
        if(em == null) return "";
        else return em.trim();
    }
    
    public String getStatus() throws Exception {
        if (this.job == null)
            throw new Exception("Call refreshConcreteJob first.");
        /**
         * Values include the following:
         *   "OK"
         *   "Error"
         *   "Degraded"
         *   "Unknown"
         *   "Pred Fail"
         *   "Starting"
         *   "Stopping"
         *   "Service"
         *   "Stressed"
         *   "NonRecover"
         *   "No Contact"
         *   "Lost Comm"
         * */
        return job.getProperty("Status").getSimpleValue();
    }
    
    public String getJobStatus() throws Exception {
        if (this.job == null)
            throw new Exception("Call refreshConcreteJob first.");
        return job.getProperty("JobStatus").getSimpleValue();
    }
    
    @Override
    public String toString() {
        try {
            return this.getStatusDescriptions() + "\n" + this.getErrorSummaryDescription() + "\n" +
                this.getErrorDescription();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
