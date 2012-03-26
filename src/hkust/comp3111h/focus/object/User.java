package hkust.comp3111h.focus.object;

public class User {

  private long id;
  private String username;
  private String password;

  public User() {
    this.setId(-1);
    this.setUsername("");
    this.setPassword("");
  }

  public User(long id, String username, String password) {
    this.setId(id);
    this.setUsername(username);
    this.setPassword(password);
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return this.username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return this.password;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
