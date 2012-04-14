/**
 * A simple class to present the time item
 */

package hkust.comp3111h.focus.database;
import org.joda.time.DateTime;

public class TimeItem {
  //Follow the schema
  long mTimeId;
  DateTime mStartTime;
  DateTime mEndTime;
  int mStatus;
  long mTaskId;
  
  public TimeItem() {
    mTimeId = 0;
    mStartTime = null;
    mEndTime = null;
    mStatus = 0;
    mTaskId = 0;
  }
  public TimeItem(
      long tTimeId,
      DateTime tStartTime,
      DateTime tEndTime,
      int tStatus, 
      long tTaskId) {
    mTimeId = tTimeId;
    mStartTime = tStartTime;
    mEndTime = tEndTime;
    mStatus = tStatus;
    mTaskId = tTaskId;
  }
  /**
   * overloaded function to initialize a TimeItem.
   * @param tTimeId
   * @param tStartTime
   * @param tEndTime
   * @param tTaskId
   */
  public TimeItem(
      long tTimeId,
      String tStartTime,
      String tEndTime,
      int tStatus,
      long tTaskId) {
    mTimeId = tTimeId;
    mStartTime = DateTime.parse(tStartTime);
    mEndTime = DateTime.parse(tEndTime);
    mStatus = tStatus;
    mTaskId = tTaskId;
  }
  
  public long timeId(){
    return mTimeId;
  }
  
  public DateTime startTime(){
    return mStartTime;
  }
  
  public DateTime endTime(){
    return mEndTime;
  }
  
  public int status(){
    return mStatus;
  }
  
  public long taskId(){
    return mTaskId;
  }
  
  public void timeId(long tTimeId){
    mTimeId = tTimeId;
  }
  
  public void startTime(DateTime startTime){
    mStartTime = startTime;
  }
  
  public void startTime(String startTime){
    mStartTime = DateTime.parse(startTime);
  }
   
  public void endTime(DateTime endTime){
    mEndTime= endTime;
  }
  
  public void endTime(String endTime){
    mEndTime = DateTime.parse(endTime);
  }
  
  public void status(int status){
    mStatus = status;
  }
  
  public void taskId(long taskId){
    mTaskId = taskId;
  }
}
