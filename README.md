#DELDROID: Determination and Enforcement of Least-Privilege Architecture in Android</h2>

###
DELDroid is an automated system for determination of least privilege architecture in Android and its enforcement at runtime. 
A key contribution of our approach is the ability to limit the privileges granted to apps without the need to modify them.

DELDroid utilizes static program analysis techniques to extract the exact privileges each component needs for providing its functionality. 
A <i>Multiple-Domain Matrix</i> representation of the system's architecture is then used to automatically analyze the security posture of the system and derive its least-privilege architecture. 

A security architect can further adapt the architecture to establish the proper privileges for each component. 
Our experiments on hundreds of real-world apps corroborate DELDroid's ability in effective enforcement of least privilege architecture and detection of security vulnerabilities with negligible overhead. 


##Approach Overview
![Alt text](/deldroid_approach.png?raw=true "Overview of DELDroid")
