## Enable Citrix debug log

To enable the maximum level of debugging, a debugging setting must be incorporated in the log4j2 configuration file.

In the log4j2.xml file inside the JMeter bin folder, insert the following line inside the `<Loggers>` clause.

```xml
<Logger name = "com.blazemeter.jmeter.citrix" level = "debug" />
```

Alternatively it can be activated when starting JMeter indicating the log level by argument

```
jmeter -Lcom.blazemeter.jmeter.citrix=DEBUG
```

## Enable thread trace on JMeter log files

In the JMeter default log configuration, it's not possible to associate each line to which virtual user generate that line.
To achieve this, the thread identifier must be incorporated into the log pattern.

In the log4j2.xml file inside the JMeter bin folder.
Locate and update the text inside the `<pattern>` tag incorporating the thread identifier `%tid` between the values `%d` and `%p`.

Example:
```xml
<File name="jmeter-log" fileName="${sys:jmeter.logfile:-jmeter.log}" append="false">
      <PatternLayout>
        <pattern>%d %tid %p %c{1.}: %m%n</pattern>
      </PatternLayout>
    </File>
    <GuiLogEvent name="gui-log-event">
      <PatternLayout>
        <pattern>%d %tid %p %c{1.}: %m%n</pattern>
      </PatternLayout>
    </GuiLogEvent>
```

## Enable Write results to file on View Results Tree listener

The default Citrix Plugin template provides a View Results Tree listener for the main playback thread group as well as one inside Citrix Recorder.

In order to provide additional debug information, you must enable write the `JMeter Text Log (JTL)` files in their maximum verbosity and in xml format.

In the `filename` field provide the name of the xml file where the log information will be stored.

**Make sure** the configuration section contains all the options activated. Go to `Configure` and check all options.

## Save JMeter console output in a log file

There are JMeter-level operations that emit debug information outside of the log4j2 logging system.
In order to capture this output, the JMeter output must be redirected to a log file.

To be able to do it, you must run JMeter from command line incorporating at the end the redirection to a file

Example:
```
jmeter >jmeter-console.log 2>&1 
```





 
