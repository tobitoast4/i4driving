package org.opentrafficsim.i4driving.messages;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.Immutable;
import org.djutils.immutablecollections.ImmutableArrayList;
import org.djutils.immutablecollections.ImmutableLinkedHashMap;
import org.djutils.immutablecollections.ImmutableList;
import org.djutils.immutablecollections.ImmutableMap;

import com.google.gson.reflect.TypeToken;

/**
 * Class containing information for scenario control over GTUs.
 * @author wjschakel
 */
public class Commands
{

    /** Type for GSON. */
    public static Type COMMANDS = new TypeToken<Commands>() {}.getType();
    
    /** GTU id. */
    private String gtuId;

    /** Generation information. */
    private GenerationInfo generate;

    /** List of commands. */
    private List<Command> commands;

    /**
     * Returns the GTU id.
     * @return String; GTU id.
     */
    public String getGtuId()
    {
        return this.gtuId;
    }

    /**
     * Returns the generation info.
     * @return GenerationInfo; generation info, {@code null} if GTU should be found from another source.
     */
    public GenerationInfo getGenerate()
    {
        return this.generate;
    }

    /**
     * Commands to give to the GTU.
     * @return ImmutableList&lt;Command&gt;; commands to give to the GTU.
     */
    public ImmutableList<Command> getCommands()
    {
        return new ImmutableArrayList<>(this.commands, Immutable.WRAP);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Commands [gtuId=" + gtuId + "]";
    }

    /**
     * Class containing info for generation of the GTU.
     * @author wjschakel
     */
    public class GenerationInfo
    {
        /** Initial speed. */
        private Speed initialSpeed;

        /** Initial position. */
        private Position initialPosition;

        /** Destination node id. */
        private String destination;

        /** Parameters to initialize the GTU with. */
        private Map<String, String> parameters;

        /**
         * Returns the initial speed.
         * @return Speed; initial speed.
         */
        public Speed getInitialSpeed()
        {
            return this.initialSpeed;
        }

        /**
         * Returns the initial position.
         * @return Position; initial position.
         */
        public Position getInitialPosition()
        {
            return this.initialPosition;
        }

        /**
         * Returns the destination node id.
         * @return String; destination node id;
         */
        public String getDestination()
        {
            return this.destination;
        }

        /**
         * Returns the parameters in a map where key are the fully specified parameters field, e.g.
         * {@code org.opentrafficsim.base.parameters.ParameterTypes.A}, and the values are a {@code String} that can be parsed
         * to a value.
         * @return
         */
        public ImmutableMap<String, String> getParameters()
        {
            return new ImmutableLinkedHashMap<>(this.parameters, Immutable.WRAP);
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "Generate [initialSpeed=" + initialSpeed + ", initialPosition=" + initialPosition + ", destination="
                    + destination + ", parameters=" + parameters + "]";
        }

    }

    /**
     * Class for a position.
     * @author wjschakel
     */
    public class Position
    {
        /** X-position. */
        private Length x;

        /** Y-position. */
        private Length y;

        /** Z-position. */
        private Length z;

        /**
         * Returns the x-position.
         * @return Length; x-position.
         */
        public Length getX()
        {
            return this.x;
        }

        /**
         * Returns the y-position.
         * @return Length; y-position.
         */
        public Length getY()
        {
            return this.y;
        }

        /**
         * Returns the z-position.
         * @return Length; z-position.
         */
        public Length getZ()
        {
            return this.z;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "Position [x=" + x + ", y=" + y + ", z=" + z + "]";
        }

    }

    /**
     * Class containing information of a command.
     * @author wjschakel
     */
    public class Command
    {
        /** Time the command should be given. */
        private Time time;

        /** Type of the command. */
        private CommandType type;

        /** Data for the command. */
        private Map<String, String> data;

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "Command [time=" + time + ", type=" + type + ", data=" + data + "]";
        }

        /**
         * Returns the time for the command.
         * @return Time; time for the command.
         */
        public Time getTime()
        {
            return this.time;
        }
        
        /**
         * Returns the command type.
         * @return CommandType; command type.
         */
        public CommandType getType()
        {
            return this.type;
        }

        /**
         * Returns the data under the name of the given field.
         * @param field String; name of the data field.
         * @return String; value of the data.
         * @throws NoSuchFieldException if no field is present under the given name.
         */
        public String getData(final String field) throws NoSuchFieldException
        {
            Throw.when(this.data == null || !this.data.containsKey(field), NoSuchFieldException.class, "No field %s.", field);
            return this.data.get(field);
        }
    }

    /**
     * Types of commands that can be given. 
     * @author wjschakel
     */
    public enum CommandType
    {
        /** Sets a parameter value (parameter/value). */
        SET_PARAMETER("setParameter"),
        
        /** Sets desired speed (speed). */
        SET_SPEED("setSpeed"),
        
        /** Resets the desired speed to regular operation. */
        RESET_SPEED("resetSpeed"),
        
        /** Sets the acceleration. (acceleration). */
        SET_ACCELERATION("setAcceleration"),
        
        /** Resets the acceleration to regular operation. */
        RESET_ACCELERATION("resetAcceleration"),
        
        /** Disables lane changes. */
        DISABLE_LANE_CHANGES("disableLaneChanges"),
        
        /** Resets the lane change behavior to regular operations. */
        ENABLE_LANE_CHANGES("enableLaneChanges"),
        
        /** Initiates a lane change. */
        CHANGE_LANE("changeLane");

        /** Print value for in JSON file. */
        private String printValue;

        /**
         * Constructor.
         * @param printValue String; print value for in JSON file.
         */
        private CommandType(final String printValue)
        {
            this.printValue = printValue;
        }

        /**
         * Returns the print value for in a JSON file.
         * @return String; print value for in a JSON file.
         */
        public String printValue()
        {
            return this.printValue;
        }
    }
}
