# JMeter Citrix Plugin Requirements

## System requirements and compatibility

### OS supported

**Windows only**
  * Windows Vista(1), 7, 8.1, 10
  * Windows Server 2008 R2(1), 2012 R2, 2016, 2019

   (1) OS Deprecated for Citrix Workspace

### Citrix recommended versions

**Server**
  * XenApp and XenDesktop 5.x or later
  * Citrix Virtual Apps and Desktops 1808 or later
  * StoreFront 2.1 or later
        
**Citrix client**
  * Citrix Receiver 4.12
  * Citrix Workspace 19.11 and later

## Apache JMeter pre-requisites

### Java version 
* Install a **JDK 8 Windows x86** (not 64 bits !) in last minor update from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Install Apache JMeter from [here](https://jmeter.apache.org/download_jmeter.cgi). Last version is advised (5.2.1)

### Microsoft Visual C++ Redistributable for Visual Studio 2019

* Install **Microsoft Visual C++ Redistributable for Visual Studio 2019** available from here:

- https://support.microsoft.com/fr-fr/help/2977003/the-latest-supported-visual-c-downloads

## Citrix Client Machine (Test Environment)

#### Citrix Client setup

- Citrix Receiver 4.12 (versions below this version have bugs, plugin does not work correctly)
- Citrix Workspace 19.11 or later

If you don't have yet Citrix client install it from [Citrix Workspace download page](https://www.citrix.com/downloads/workspace-app/windows/workspace-app-for-windows-latest.html) or [Citrix Receiver download page](https://www.citrix.com/downloads/citrix-receiver/windows/receiver-for-windows-latest.html)

#### Disable UAC (User Account Control)

- See: https://richardstk.com/2013/12/17/fully-disable-user-account-control-in-windows-server-2012-windows-server-2012-r2/

#### Ensure consistency between machines

If you intend to replay the script on another machine which will be the case for BlazeMeter, make sure that the following items are consistent between the record and BlazeMeter replay machines: 

- Window Size (screen resolution), 
- Window Colors and color depth, 
- System Font, 
- ClearType, 
- and the other Default Options settings for the Citrix client. 

**These settings affect the hash values and screen coordinates (for OCR or HASH), and inconsistencies will cause sampling to fail**. 

To view the Citrix client settings, right-click an item from the Citrix program group and select Application Set Settings or Custom Connection Settings. 
(Note that the remote session on the Citrix server inherits the ClearType settings of the local machine.)

Display settings of 1024 x 768 are recommended.

Alternatively, you can use the following JMeter parameters to try to set display settings of the Citrix session (See citrix.properties file for futher information):

* bzm.citrix.client_factory.client_property.horizontal_resolution 
* bzm.citrix.client_factory.client_property.vertical_resolution
* bzm.citrix.client_factory.client_property.color_depth

Keep in mind that Citrix server settings have precedence on the above parameters.

#### Prevent automatic Citrix start

Close all instances of the **concentr.exe** process for all users. 
To prevent the Citrix Connection Center from starting automatically, set the ConnectionCenter registry key to an empty value. 
This key can be found at:

     * 32-bit systems: HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Run
     * 64-bit systems: HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Run

Connection Center also generates performance issues when observing the status of all sessions on the client machine.
If you want to have a higher performance, it is recommended to disable it from its start or eliminate the process from its execution (taskkill or taskmanager)

#### Citrix Receiver Security Warning

The Citrix client may prompt you with a warning "An online application is attempting to access files in your computer". This dialog box blocks the replay because it requires user intervention.
Workaround: To prevent this, configure the registry on the Citrix client machine to allow it to silently access local drives, as described [here](http://support.citrix.com/article/CTX124921).

#### Citrix Receiver Disable Session Scaling

By default, the Citrix client scales the session adapting it to the resolution of the client's machine.
Scaling operation causes blurry images or differences on others machines with different DPI resolution, causing Image Hash and OCR operations to fail.

It is recommended to disable high resolution DPI scaling for proper recording and execution. 
Follow the instructions mentioned on the page: : [Image Hash or OCR fails on different machine resolution](TROUBLESHOOTING.md#image-hash-or-ocr-fails-on-different-machine-resolution)

#### Security Software

If possible, disable anti-malware and other security or antivirus software. Alternatively, add an exception to ignore JMeter process and ICA Client receiver.

#### Firewall

If a firewall is present between your injector and target citrix platform, ensure you open required firewall ports:

- https://docs.citrix.com/en-us/receiver/windows/current-release/secure-communication/connect-through-firewall.html

#### Antivirus

An antivirus or Windows Defender can parasitize injector load, ensure you add exclusion rules.

## Tips on Test environment and setup

#### Windows Style
 
Record all windows in the "classic" windows style—not the XP style. This is relevant when using Hash. 

To change the Windows style to "classic": 

* Click in the desktop area. 
* Choose Properties from the right-click menu. 
* Select the Theme tab
* Choose Windows Classic from the Theme drop down list. 
* Click OK. 

In modern versions of Windows it is not possible to change the theme to Classic.
It is recommended in any version of Windows, disable the visual effects for better consistency and performance. 
Follow the instructions in: [TROUBLESHOOTING](TROUBLESHOOTING.md#high-cpu-usage-on-dwmexe-process)

#### Virtual Machine
If running JMeter on virtual machines, check that you're dedicating memory and processes. This is a general recommendation and not Citrix specific.

## Citrix Server config recommendations

*Ensure that you are working with supported versions of your Citrix server.*

#### Session Disconnect

By default, when a client times out or disconnects from the Citrix server, the session remains open for a defined time period. 
However, beginning a run in a Citrix session that has an unpredictable state can cause your test to fail.
Therefore, the Citrix server administrator should configure the Citrix server to end (reset) the client session when a client disconnects for any reason.

#### Multi-Session Support

If you are going to run more than one Citrix Session on JMeter, ensure that the Citrix server is configured to enable multiple sessions per user/client.

#### Avoid production environments if possible
Try to load test Citrix applications which are restricted to a few Citrix servers in a Citrix development or test environment rather than load testing in a live Citrix production environment.


