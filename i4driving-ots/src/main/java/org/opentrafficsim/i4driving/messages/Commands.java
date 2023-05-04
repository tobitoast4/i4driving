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
    public static Type COMMANDS = new TypeToken<Commands>()
    {
    }.getType();

    /** GTU id. */
    private String gtuId;

    /** Generation information. */
    private GenerationInfo generationInfo;

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
    public GenerationInfo getGenerationInfo()
    {
        return this.generationInfo;
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
        /** Generation time. */
        private Time time;

        /** Initial speed. */
        private Speed initialSpeed;

        /** Initial position. */
        private LanePosition initialPosition;

        /** GTU type. */
        private String gtuType;

        /** Destination node id. */
        private String destination;

        /** Parameters to initialize the GTU with. */
        private Map<String, String> parameters;

        /**
         * Returns the generation time.
         * @return Time; generation time.
         */
        public Time getTime()
        {
            return this.time;
        }

        /**
         * Returns the initial speed.
         * @return Speed; initial speed.
         */
        public Speed getInitialSpeed()
        {
            return this.initialSpeed;
        }

        /**
         * Returns the GTU type id.
         * @return String; GTU type id.
         */
        public String getGtuType()
        {
            return gtuType;
        }

        /**
         * Returns the initial position.
         * @return LanePosition; initial position.
         */
        public LanePosition getInitialPosition()
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
            return "Generate [initialSpeed=" + this.initialSpeed + ", initialPosition=" + this.initialPosition
                    + ", destination=" + this.destination + ", parameters=" + this.parameters + "]";
        }

    }

    /**
     * Class for a position.
     * @author wjschakel
     */
    public class LanePosition
    {
        /** Lin id. */
        private String link;

        /** Lane id. */
        private String lane;

        /** X-position on the lane. */
        private Length x;

        /**
         * Returns link id.
         * @return String; link id.
         */
        public String getLink()
        {
            return this.link;
        }

        /**
         * Returns the lane id.
         * @return String; lane id.
         */
        public String getLane()
        {
            return this.lane;
        }

        /**
         * Returns the x-position.
         * @return Length; x-position.
         */
        public Length getX()
        {
            return this.x;
        }

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "LanePosition [link=" + this.link + ", lane=" + this.lane + ", x=" + this.x + "]";
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

        /** {@inheritDoc} */
        @Override
        public String toString()
        {
            return "Command [time=" + this.time + ", type=" + this.type + ", data=" + this.data + "]";
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
        SET_DESIRED_SPEED("setDesiredSpeed"),

        /** Resets the desired speed to regular operation. */
        RESET_DESIRED_SPEED("resetDesiredSpeed"),

        /** Sets the acceleration. (acceleration). */
        SET_ACCELERATION("setAcceleration"),

        /** Resets the acceleration to regular operation. */
        RESET_ACCELERATION("resetAcceleration"),

        /** Disables lane changes. */
        DISABLE_LANE_CHANGES("disableLaneChanges"),

        /** Resets the lane change behavior to regular operations. */
        ENABLE_LANE_CHANGES("enableLaneChanges"),

        /** Initiates a lane change. */
        CHANGE_LANE("changeLane"),

        /** Set indicator. */
        SET_INDICATOR("setIndicator");

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
