# Java File Monitor
Simple Java file monitor.  Monitors file activity in the indicated paths, that's it...

1- Create the configuration:
```java
final FileMonitorConfig config = new FileMonitorConfig();
config.setPaths(new String[] { "C:\\test" });
config.setFileNameRegex(".*\\.txt");
config.setInterval(500);
config.setRecursive(false);
```

2- Implement your listener:
```java
final FileMonitorListener listener = new FileMonitorListener() {
  @Override
  public void onFileChange(final FileMonitorEvent event) {
    App.LOG.info("File changed");
  }

  @Override
  public void onFileCreate(final FileMonitorEvent event) {
    App.LOG.info("File created");
  }

  @Override
  public void onFileDelete(final FileMonitorEvent event) {
    App.LOG.info("File deleted");
  }

  @Override
  public void onStart() {
    App.LOG.info("Started");
  }

  @Override
  public void onStop() {
    App.LOG.info("Stopped");
  }
};
```

3- Start monitoring:
```java
FileMonitor monitor = new FileMonitor(config);
```

You can also do this:
```java
monitor.pause();
monitor.unpause();
monitor.stop();
```
