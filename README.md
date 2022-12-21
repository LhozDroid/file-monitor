# Java File Monitor
Simple Java file monitor.  Monitors file activity in the indicated paths, that's it...

1- Use the builder to create the file monitor object:

```java
final FileMonitor monitor = new FileMonitorBuilder()//
				.withPaths("C:/test")//
				.withInterval(500)//
				.withRecursivity(false)//
				.withFileNameRegex(".*\\.txt")//
				.build();
```

2- Add some listeners

```java
monitor.add(listener);
```

3- Start the monitor:

```java
monitor.start();
```

You can also do this:

```java
monitor.pause();
monitor.unpause();
monitor.stop();
```

<a href="https://www.paypal.com/donate/?cmd=_donations&business=CSQRVLE2D43NU&item_name=Buy+me+a+beer!&currency_code=USD">
  <strong>Buy me a beer!</strong>
</a>
