## Install Plugin

#### From JMeter-Plugins

* Install plugins-manager from [here](https://jmeter-plugins.org/install/Install/)
* Start it and search for Citrix then install

#### Manual install from JMeter-Plugins or Releases

##### Download Citrix Plugin from JMeter-Plugins #####
Go to the following [link](https://jmeter-plugins.org/?search=bzm-jmeter-citrix-plugin) and download the plugin zip file

* Uncompress bzm-jmeter-citrix-plugin-[version].zip archive to get the jar file

##### Download Citrix Plugin from GitHub Releases #####
Go to the following [link](https://github.com/Blazemeter/CitrixPlugin/releases) and download the latest plugin jar file

##### Install the Citrix Plugin jar file into JMeter #####
* Copy citrix-jmeter-[version].jar in JMETER_HOME/lib/ext 

    Since version 1.0, the plugin provides an installer which checks and sets up the next step. If you encounter any errors, please check this point manually.

* Edit jmeter/bin/saveservices.properties and add at the end of file
```
        # Add the following line at the end of JMeter saveservice.properties file
        _com.blazemeter.jmeter.citrix.sampler.CitrixSampleResultConverter=collection
```
## Run JMeter 

Once installed, the first time start JMeter as **as administrator**, see below, it will automatically run required installation steps.
If you encounter any errors, please check below points manually.

### Requisites to check **only if automatic installation fails**:

* **Ensure you run below commands as Administrator**

* Once Citrix Receiver is installed, register ICA Client COM Object, run **as administrator** : 
```
    regsvr32 /s "C:\Program Files (x86)\Citrix\ICA Client\wfica.ocx"
```
* Add registry keys :
    
   You can click on setup/setup-x86.reg or setup/setup-x64.reg to add the below keys:

    * For Windows 32bits: 
        * Location: HKEY_LOCAL_MACHINE\Software\Citrix\ICA Client\CCM 
        * Name: AllowSimulationAPI 
        * Type: REG_DWORD 
        * Data: 1
        
        * Location: HKEY_LOCAL_MACHINE\SOFTWARE\Citrix\ICA Client\ 
        * Name: VdLoadUnLoadTimeOut
        * Type: REG_DWORD 
        * Data: 30
        
    * For Windows 64bits:
        * Location: HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Citrix\ICA Client\CCM
        * Name: AllowSimulationAPI 
        * Type: REG_DWORD 
        * Data: 1
        
        * Location: HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432node\Citrix\ICA Client 
        * Name: VdLoadUnLoadTimeOut
        * Type: REG_DWORD 
        * Data: 30 (Value in seconds (Decimal))

   The second key VdLoadUnLoadTimeOut is needed to workaround an issue that manifests as :
   
      "You might be having an issue with a Citrix virtual driver (SmartCard). Contact your system administrator for further assistance":

   See
   
- https://support.citrix.com/article/CTX133536

### Plugin Properties

You can adjust plugin properties by adding an entry in JMeter file located in jmeter/bin/user.properties.

Here is the list of configurable properties,  **non bold** properties should be ok in all case, you may want to adjust the **bold** ones :

| Property name | Description  | Default Value |
|------------   |-----------   |------------   |
| bzm.citrix.client_factory.client_property.horizontal_resolution | Screen horizontal resolution |  |
| bzm.citrix.client_factory.client_property.vertical_resolution | Screen vertical resolution |  |
| bzm.citrix.client_factory.client_property.color_depth | Color depth, use Color16 to get 16 colors, Color256 to get 256 color, Color16Bit to get high colors (16bpp), Color24Bit to get true colors (24bpp), Keep it empty to let the client decide | |        
| bzm.citrix.client_factory.client_property.socket_timeout_ms | JMeterProperty that defines the socket timeout of Receiver in Milliseconds | 5000 (in millis) |  |
| bzm.citrix.clause_check_interval | Interval for the timing of clause checks | 1000 (in millis) |
| bzm.citrix.clause_check_timeout | Default time period (in ms) during which a clause must be validated | 3000 (in millis) |
| bzm.citrix.clause_check_max_results | Maximum number of check results kept in the responseMessage  | 20  |
| bzm.citrix.clause_hash_bit_resolution | Hash resolution in bits | 128 (in bits) | 
| bzm.citrix.clause_hash_bit_distance | Hash Hamming Distance in bits | 3 (in bits) |
| bzm.citrix.client_factory.client_property.icafile_timeout_ms | Maximum wait time for ICAFile connect event at application start | 10000 (in millis) |
| bzm.citrix.client_factory.client_property.connect_timeout_ms | Maximum wait time for CONNECT session event at application start | 15000 (in millis) |
| bzm.citrix.client_factory.client_property.logon_timeout_ms   | Maximum wait time for LOGON session event at application start | 60000 (in millis) |
| bzm.citrix.client_factory.client_property.logoff_timeout_ms  | Maximum wait time for LOGOFF session event at application end | 65000 (in millis) |
| bzm.citrix.client_factory.client_property.disconnect_timeout_ms  | Maximum wait time for DISCONNECT session event at application end | 65000 (in millis) |
| **bzm.citrix.default_recording_import_path** | Directory used to decompress during import recording | <JMeterHome>/citrix-recordings/<folder with date and time> |
| bzm.citrix.ica_downloading_ignore_backends | Allows you to ignore the BackendListeners present in the plan during ICA download | true |
| bzm.citrix.ica_downloading_ignore_timers | Allows you to ignore timers present in the test plan during ICA download | true |
| bzm.citrix.ica_file_path_var | Refers to the variable used to retrieve the ICA path by the ICA File Saver | citrix_ica_file_path |
| **bzm.citrix.ica_recording_folder** | Default recording folder | <JMeterHome>/citrix_recording |
| **bzm.citrix.ica_files_folder** | Property used to set default value for **ICA File Saver** element. This value will be used as Folder where ICA files are downloaded. Make sure that user running JMeter is allowed to **read/write** in this folder. | <JMeterHome>/ica_files |
| **bzm.citrix.selection_color** | Color of the selection mask expressed as R,G,B | 0,255,0 which is Green |
| **bzm.citrix.hightlight_color** | Color of the highlight mask expressed as R,G,B | 255,0,0 which is Green |
| **bzm.citrix.ocr_language** | Language used for text recognition | eng |
| **bzm.citrix.ocr_data_path** | Folder containing data for OCR recognition | temp folder |
| **bzm.citrix.ocr_default_dpi** | Images default dpi used by OCR | 70 |
| **bzm.citrix.capture_max_size** | The maximum number of user interactions that can be recorded on the same capture WARNING !!! **A value too high can lead to insufficient memory** | 500 |
| **bzm.citrix.ica_downloading_ignore_timers** | Ignore timers when downloading ICA during recording | true |
| **bzm.citrix.ica_downloading_ignore_backends** | Ignore timers when downloading ICA during recording | true |
| **bzm.citrix.keystroke_delay** | Default delay between keystrokes | 100 |
| **bzm.citrix.keystroke_delay_variation** | Default variation added to delay between keystrokes | 10                                        |


