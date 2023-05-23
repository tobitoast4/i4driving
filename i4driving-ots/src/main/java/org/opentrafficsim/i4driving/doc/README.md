# Cut in scenario manual

## Compiling and executing
Compile a runnable .jar file that will run the main class org.opentrafficsim.i4driving.ScenarioCutIn. In Eclipse this can be 
done by following File > Export... and following the further instructions. To run the simulation invoke the command: 
`java -jar NameOfFile.jar`. Java 11 is required for this. If this is not available on the computer, a JRE can be placed 
next to the runnable .jar file, and the `java` command can point to the java executable in that JRE as follows: 
`jre-11\bin\java -jar NameOfFile.jar`.

## Input
### Command line arguments and settings file
Input can be given in two manners. Command line arguments can be added to the command as follows:
`java -jar NameOfFile.jar --autorun=true --simulatioTime=45s`

Alternatively, a settings file can be specified in json format:
<pre>
{
    "settings": {
        "autorun": "true",
        "simulationTime": "45s",
    }
}
</pre>

Note that the program needs to be able to access this file. The program will first attempt to read the file `settings.json` 
which should be placed next to the runnable .jar file. If this is not found, it will read the command line argument 
`--settings` which has value `cutinSettings.json` by default. If this too cannot be found as a file, the program will use
a file under this name in its own resources.

Depending on preferences, the program can thus be used in 3 manners:
<ol>
  <li>Command line arguments.</li>
  <li>Command line argument `settings` pointing to a file for all other input.</li>
  <li>Swapping file `settings.json` for each run.</li>
</ol>

### Input settings
These settings are available as command line arguments and as settings in a settings file. Note that as command line arguments,
the names need to be preceded with "`--`".
<table>
  <tr><td><b>Argument</b></td><td><b>Default</b></td><td><b>Description</b></td></tr>
  <tr><td colspan="3" bgcolor="#DDDDDD"><i>Simulation</i></td></tr>
  <tr><td>autorun</td><td>false</td><td>When false, a GUI is shown in to which the play button must be pressed to run the 
    simulation. When true, the simulation runs automatically without GUI.</td></tr>
  <tr><td>seed</td><td>1</td><td>Random seed value.</td></tr>
  <tr><td>simulationTime</td><td>60s</td><td>Total simulation time.</td></tr>
  <tr><td colspan="3" bgcolor="#DDDDDD"><i>Input and output files</i></td></tr>
  <tr><td>settings</td><td>settings.json</td><td>File containing any other settings.</td></tr>
  <tr><td>inputVehicle1</td><td>cutinVehicle1.json</td><td>File containing instructions for vehicle 1.</td></tr>
  <tr><td>inputVehicle2</td><td>cutinVehicle2.json</td><td>File containing instructions for vehicle 2.</td></tr>
  <tr><td>inputVehicle3</td><td>cutinVehicle3.json</td><td>File containing instructions for vehicle 3.</td></tr>
  <tr><td>outputTrajectoriesFile</td><td>outputTrajectories.csv</td><td>File for output trajectories.</td></tr>
  <tr><td>outputValuesFile</td><td>outputValues.csv</td><td>File containing output values such as max deceleration.</td></tr>
  <tr><td colspan="3" bgcolor="#DDDDDD"><i>Imperfect perception</i></td></tr>
  <tr><td>fullFuller</td><td>true</td><td>Implements imperfect perception. Overwrites all perception settings to true.</td></tr>
  <tr><td>fuller</td><td>true</td><td>Implements imperfect perception.</td></tr>
  <tr><td>carFollowingTask</td><td>true</td><td>Implements task demand from car-following.</td></tr>
  <tr><td>laneChangeTask</td><td>true</td><td>Implements task demand from lane changing.</td></tr>
  <tr><td>laneChangeIsPrimary</td><td>true</td><td>Sets lane change as primary for anticipation reliance, meaning anticipation 
    is relied upon for car-following. When false, car-following is primary.</td></tr>
  <tr><td>anticipationReliance</td><td>true</td><td>Enables anticipation reliance, which lowers peak demands that would result 
    from simple task demand summation.</td></tr>
  <tr><td>adaptHeadway</td><td>true</td><td>Headway is increased when task saturation is high.</td></tr>
  <tr><td>adaptSpeed</td><td>true</td><td>Speed is lowered when task saturation is high.</td></tr>
  <tr><td>multiAnticipation</td><td>true</td><td>Anticipate multiple leaders in car-following.</td></tr>
  <tr><td colspan="3" bgcolor="#DDDDDD"><i>Social interactions</i></td></tr>
  <tr><td>fullSocio</td><td>true</td><td>Applies social interactions. Overwrites all socio settings to true.</td></tr>
  <tr><td>socio</td><td>true</td><td>Applies social interactions.</td></tr>
  <tr><td>tailgating</td><td>true</td><td>Implements tailgating to express social pressure.</td></tr>
  <tr><td>socioLaneChangeIncentive</td><td>true</td><td>Makes drivers get or stay out of the way through (not) changing lane.
    </td></tr>
  <tr><td>socioDesiredSpeed</td><td>true</td><td>Makes drivers increase speed when tailgated.</td></tr>
</table>

### Vehicle control
There are 3 input file for vehicles. Please refer to the java resources folder for examples of these files. In particular the
file `vehicle1.json` shows all commands that can be given, such as setting the desired speed, or initiating a lane change.
All these commands overwrite the normal model, for as long as the command remains active. For example, you can set, but also 
reset, the desired speed. Or you can temporarily disable lane changes. Note that a command to change lane from an input file is 
executed even if lane changes are disabled. The disabling only applies to lane changes that the normal model would otherwise
perform.

### Parameters
In the vehicle input files, parameters can be set at vehicle generation, or at some time during the scenario. The full path to a
parameter needs to be provided, e.g. `org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters.SOCIO`.
For a complete list, please refer to the excel file of OTS parameter as provided within the project.