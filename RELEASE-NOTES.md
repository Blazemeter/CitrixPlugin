# 0.1.0 (DELIVERED ON 28 november 2018)

## Scope: 

* Lot 1
* Features of Lot 2

## Features:

### Citrix Recorder for JMeter (**Lot 1**)

Controls the recording: 
* defines steps of scenario, 
* handles ICA file downloading, 
* launches and controls Citrix Recording Gateway

### Citrix Recording Gateway for Windows (**Lot 1**)

* Detects user interactions
* Allows user to define assertions.
* Sends user interactions and assertions to JMeter

### Citrix Sampling Gateway for Windows (**Lot 1**)

Gateway between JMeter and Citrix Server:
   * sends events from JMeter to Citrix
   * sends screenshot to JMeter when Citrix signals an update

### Complete Citrix Sampler / Assertions plugin for JMeter  (**Lot 1** + **Partial Lot2**)

* Launches the sampling gateway and controls its execution
* Sends events to the gateway
* Waits to match expected state expressed through HASH/OCR/Timeout from the gateway
* Checks assertions
* Saving of Recording to View Results Tree
* Reloading of Recording to View Results Tree
* Multi-Threaded Sampling
* NON-GUI Sampling

# 0.2.0 (DELIVERED ON 7 december 2018)

**Enhancements:**

* #160: Select parameterized template if version >=5
* #170: Replace MD5 Hash by Perceptive Hash for image comparison
* #153: [UX] Reduce size of Citrix Information Panel and better organize buttons
* #137: Cleanup ICA files on test startup
* #166: Create Installer that updates saveservice.properties
* #171: Improve installer to check Citrix Client Receiver is installed and required registry keys are set and warn user (Partial)
* #113: Allow configuration of mask color for selection inside images
* #169: Packaging : Distribute a fat jar

**Bug fixes:**

* #167: Citrix Assertion : Enable / Disable throwing NPE in JMeter < 5.1
* #168: EndClause : Field value is saved only when fields losses focus

# 0.2.1 (DELIVERED ON 10 december 2018)

**Enhancements:**

* #171: Improve installer to check Citrix Client Receiver is installed and required registry keys are set and warn user (Complete)

**Bug fixes:**

* Fix Packaging issue following implementation of 1 fat jar

### OCR Integration of Enterprise License from Asprise (Lot 2)

* Integration of Trial license, the enterprise license has not been provided

# 0.3.0 (DELIVERED ON 20 december 2018):

### Lot 2 Features (includes parts of 2(B)) 

* #132 Switch OCR from Asprise to Tesseract (2B)
* #178 Create descriptor for jmeter-plugins-manager and test installation through it (2B)
* #126 Show selection mask of End Clause in CitrixRenderer (2B)
* #154 [Recorder GUI]Update panel label to make them depend on state of recorder (2B)
* #156 CitrixResultRenderer : Allow "Relative to foreground window" (2B)

### Scope of 2(B) (2(A) being canceled)
Scope to be completed, ideas:
   
* Application Window tracking
* Ability to be notified of Windows events, and as a consequence, wait or assert
