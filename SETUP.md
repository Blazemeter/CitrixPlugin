## Install Plugin

#### From JMeter-Plugins

* 1 - Install plugins-manager on JMeter from [here](https://jmeter-plugins.org/install/Install/)
* 2 - When Plugins Manager was installed run JMeter with Administrator privilege, right click on jmeter.bat and select **Run as administrator**.
* 3 - On Plugin Manager (Options -> Plugin Manager), select to install the Citrix plugin from the available plugins.

When Plugin Manager installs the plugin, JMeter will restart. At that time, the Citrix plugin installation process will run.

If the setup shows an error, close JMeter again and run it again with Administrator permissions.
When the error messages no longer appear, it means that you can now close JMeter and use it with normal user privileges.

If you find any error and cannot solve it, register a ticket to be attended by support [here](https://github.com/BlazeMeter/CitrixPlugin/issues)

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
| bzm.citrix.client_factory.client_property.connect_timeout_ms | Maximum wait time for CONNECT session event at application start | 90000 (in millis) |
| bzm.citrix.client_factory.client_property.logon_timeout_ms | Maximum wait time for LOGON session event at application start | 90000 (in millis) |
| bzm.citrix.client_factory.client_property.activeapp_timeout_ms | Maximum wait time for application become active | 120000 (in millis) |
| bzm.citrix.client_factory.client_property.logoff_timeout_ms | Maximum wait time for LOGOFF session event at application end | 65000 (in millis) |
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
| **bzm.citrix.ocr_data_path** | Custom text recognition data folder with language trained data files (Tesseract data format) | user temp folder |
| **bzm.citrix.ocr_default_dpi** | Images default dpi used by OCR | 70 |
| **bzm.citrix.capture_max_size** | The maximum number of user interactions that can be recorded on the same capture WARNING !!! **A value too high can lead to insufficient memory** | 500 |
| **bzm.citrix.ica_downloading_ignore_timers** | Ignore timers when downloading ICA during recording | true |
| **bzm.citrix.ica_downloading_ignore_backends** | Ignore timers when downloading ICA during recording | true |
| **bzm.citrix.keystroke_delay** | Default delay between keystrokes | 100 |
| **bzm.citrix.keystroke_delay_variation** | Default variation added to delay between keystrokes | 10                                        |


