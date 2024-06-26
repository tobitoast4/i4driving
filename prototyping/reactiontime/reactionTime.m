% This script shows a small prototype of car-following with the IDM+ using
% a variable reaction time based on the task demand of car-following. The
% scenario can be expanded with a persistent distraction for all drivers.
% The simulation is event-based and therefore exactly follows the dynamic
% time steps of the model.
%
% Author: Wouter Schakel (w.j.schakel@tudelft.nl)
% Date: 24-06-2024

% IDM+ parameters.
a = 1.25; % maximum acceleration [m/s2]
b = 2.09; % maximum comfortable deceleration [m/s2]
T = 1.2; % desired time headway [s]
delta = 4; % acceleration exponent
v0 = 120/3.6; % desired speed [m/s]
s0 = 3; % stopping distance [m]
l = 4; % vehicle length [m]

% Scenario settings.
h = 4; % exponential decay of task demand for increasing time headway [s]
dtMax = 2; % maximum reaction time [s]
bMax = 6; % maximum deceleration [m/s2]
n = 10; % number of vehicles [-]
tBrake = 5; % time when first vehicle starts to brake [s]
bBrake = 6; % first vehicle braking deceleration [m/s2]
tAccel = 20; % time when first vehicle starts to accelerate again [s]
tEnd = 80; % end of simulation [s]
distraction = 0.0; % task demand of some distraction [0...1)

% Use 'x', 'v', 'a', 's' or 'dt' to plot position, speed, acceleration, spacing or reaction time.
field = 'dt'; 

% Initiate vehicles and events.
vehicles = struct();
events = struct();
s = s0+v0*T;
for i = 1:n
    vehicles(i).t = 0;
    vehicles(i).x = (n-i) * (s + l);
    vehicles(i).v = v0;
    vehicles(i).a = 0;
    vehicles(i).s = s;
    % task demand for car-following
    td = exp(-(s/v0)/h);
    % steady state for car-following in Markov chain (or td if lower):
    attention = min(td, (1-distraction) / (2-td-distraction));
    dt = (1 - attention) * dtMax;
    vehicles(i).dt = dt;
    if i > 1
        events.t(i - 1) = dt;
        events.vehicle(i - 1) = i;
    end
end

% Schedule first vehicle events.
if (tAccel-tBrake) > v0/bBrake
    % Need an event when leader reaches 0 speed when braking.
    tStandStill = tBrake + v0/bBrake;
else
    tStandStill = inf;
end
events.t(end+1) = tBrake;
events.vehicle(end+1) = 1;
events.t(end+1) = tStandStill;
events.vehicle(end+1) = 1;
events.t(end+1) = tAccel;
events.vehicle(end+1) = 1;
events.t(end+1) = dtMax;
events.vehicle(end+1) = 0; % triggers trajectory update to prevent big straight sections in plot

% Schedule end event.
events.t(end+1) = tEnd;
events.vehicle(end+1) = -1; % triggers simulation end

% Simulation loop.
collision = [];
events = sortEvents(events);
while events.t(1) <= tEnd

    % Pop event.
    t = events.t(1);
    ego = events.vehicle(1);
    events.t(1) = [];
    events.vehicle(1) = [];

    if ego > 1
        % Any follower.
        [xEgo, vEgo] = constantAcceleration(vehicles(ego).x(end), vehicles(ego).v(end), ...
            vehicles(ego).a(end), t - vehicles(ego).t(end));
        [xLead, vLead] = constantAcceleration(vehicles(ego-1).x(end), vehicles(ego-1).v(end), ...
            vehicles(ego-1).a(end), t - vehicles(ego-1).t(end));

        % Gap state.
        s = xLead - xEgo - l;
        if s <= 0
            collision = ego;
            vehicles = endSimulation(vehicles, t, l);
            break;
        end
        ss = s0 + vEgo*T + vEgo*(vEgo-vLead)/(2*sqrt(a*b));

        % Append current dynamics.
        vehicles(ego).t(end+1) = t;
        vehicles(ego).x(end+1) = xEgo;
        vehicles(ego).v(end+1) = vEgo;
        vehicles(ego).a(end+1) = max(-bMax, a * min(1-(vEgo/v0)^delta, 1-(ss/s)^2));
        vehicles(ego).s(end+1) = s;
        % task demand for car-following
        td = exp(-(s/vEgo)/h);
        % steady state for car-following in Markov chain (or td if lower):
        attention = min(td, (1-distraction) / (2-td-distraction));
        dt = (1 - attention) * dtMax;
        vehicles(ego).dt(end+1) = dt;
        
        % Schedule next event.
        events.t(end+1) = t + dt;
        events.vehicle(end+1) = ego;

    elseif ego == 1
        % Platoon leader.
        [xEgo, vEgo] = constantAcceleration(vehicles(ego).x(end), vehicles(ego).v(end), ...
            vehicles(ego).a(end), t - vehicles(ego).t(end));

        % Append current dynamics.
        vehicles(ego).t(end+1) = t;
        vehicles(ego).x(end+1) = xEgo;
        vehicles(ego).v(end+1) = vEgo;
        if t == tBrake
            vehicles(ego).a(end+1) = -bBrake;
        elseif t == tStandStill
            vehicles(ego).a(end+1) = 0;
        elseif t >= tAccel
            vehicles(ego).a(end+1) = a * (1-(vEgo/v0)^delta);
        end
        vehicles(ego).s(end+1) = NaN;
        vehicles(ego).dt(end+1) = dtMax;

        % Schedule next event during acceleration.
        if t >= tAccel
            events.t(end+1) = t + dtMax;
            events.vehicle(end+1) = 1;
        end

    elseif ego == 0
        % Platoon leader; only perform a time step for the trajectory.
        ego = 1;
        [xEgo, vEgo] = constantAcceleration(vehicles(ego).x(end), vehicles(ego).v(end), ...
            vehicles(ego).a(end), t - vehicles(ego).t(end));

        % Append current dynamics.
        vehicles(ego).t(end+1) = t;
        vehicles(ego).x(end+1) = xEgo;
        vehicles(ego).v(end+1) = vEgo;
        vehicles(ego).a(end+1) = vehicles(ego).a(end);
        vehicles(ego).s(end+1) = NaN;
        vehicles(ego).dt(end+1) = dtMax;

        % Schedule next event.
        if t < tAccel
            events.t(end+1) = t + dtMax;
            events.vehicle(end+1) = 0;
        end

    else
        % Simulation end, apply partial final step to all vehicles.
        vehicles = endSimulation(vehicles, t, l);
        break;

    end

    % Sort events for next loop.
    events = sortEvents(events);
end

% Plot vehicles.
for i = 1:n
    plot(vehicles(i).t, vehicles(i).(field), 'Color', [0 0 0], 'LineStyle', '-', 'Marker', '.');
    hold on;
end
if ~isempty(collision)
    plot(vehicles(collision).t(end), vehicles(collision).(field)(end), ...
        'Color', [1 0 0], 'LineStyle', 'none', 'Marker', 'x', 'MarkerSize', 10);
end
xlabel('Time [s]');
if strcmp(field, 'x')
    ylabel('Position [m]');
elseif strcmp(field, 'v')
    ylabel('Speed [m/s]');
elseif strcmp(field, 'a')
    ylabel('Acceleration [m/s^2]');
elseif strcmp(field, 's')
    ylabel('Net distance gap [m]');
elseif strcmp(field, 'dt')
    ylabel('Reaction time [s]');
end

% Show macroscopic properties.
yLim = get(gca, 'YLim');
if isempty(collision)
    K = (n-1) / (vehicles(1).x(1)-vehicles(end).x(1));
    Q = K * v0;
    Ksat = (n-1) / (vehicles(1).x(end)-vehicles(end).x(end));
    Qsat = Ksat * (vehicles(1).v(end)+vehicles(1).v(1))/2;
    text(0, yLim(2), 0, sprintf(' K=%.1f veh/km, Q=%.0f veh/h', K*1000, Q*3600), ...
        'VerticalAlignment', 'top', 'HorizontalAlignment', 'left')
    text(tEnd, yLim(1), 0, sprintf('K=%.1f veh/km, Q=%.0f veh/h ', Ksat*1000, Qsat*3600), ...
        'VerticalAlignment', 'bottom', 'HorizontalAlignment', 'right')
else
    text(0, yLim(2), 0, ' Collision', ...
        'VerticalAlignment', 'top', 'HorizontalAlignment', 'left')
end

% Sorts events by time.
function events = sortEvents(events)
    [events.t, inds] = sort(events.t);
    events.vehicle = events.vehicle(inds);
end

% Apply a constant acceleration.
function [x, v] = constantAcceleration(x0, v0, a, dt)
    if a < 0
        dt = min(dt, v0 / -a);
    end
    x = x0 + dt*v0 + .5*a*dt^2;
    v = v0 + dt * a;
end

% Appends a final point to all vehicles at the end time.
function vehicles = endSimulation(vehicles, t, l)
    for ego = 1:length(vehicles)
        [xEgo, vEgo] = constantAcceleration(vehicles(ego).x(end), vehicles(ego).v(end), ...
            vehicles(ego).a(end), t - vehicles(ego).t(end));
        if ego > 1
            [xLead, ~] = constantAcceleration(vehicles(ego-1).x(end), vehicles(ego-1).v(end), ...
                vehicles(ego-1).a(end), t - vehicles(ego-1).t(end));
            s = xLead - xEgo - l;
        else
            s = NaN;
        end
        vehicles(ego).t(end+1) = t;
        vehicles(ego).x(end+1) = xEgo;
        vehicles(ego).v(end+1) = vEgo;
        vehicles(ego).a(end+1) = vehicles(ego).a(end);
        vehicles(ego).s(end+1) = s;
        vehicles(ego).dt(end+1) = vehicles(ego).dt(end);
    end
end