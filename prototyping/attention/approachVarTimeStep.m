% This script simulates an example approach with variable car-following 
% time step.
%
% Author: Wouter Schakel (w.j.schakel@tudelft.nl)
% Date: 20-09-2024

a = 1.25;
b = 2.09;
T = 1.2;
s0 = 3;
v0 = 120/3.6;

dt_min = 0.0;
dt_max = 3.0;
h = 4;

v_0 = v0;
s_0 = 300;
v_lead = 50 / 3.6;

t = 0;
v = v_0;
s = s_0;
g = [];

while v(end) > 0.001 && t(end) < 60.0
    ss = s0 + v(end)*T + v(end)*(v(end)-v_lead)/(2*sqrt(a*b));
    g(end+1) = a*min(1 - (v(end)/v0)^4, 1 - (ss/s(end))^2);

    headway = s(end) / v(end);
    TD = exp(-headway/h);
    dt = dt_max - TD * (dt_max-dt_min);

    %dt = 0.5;

    t(end+1) = t(end) + dt;
    if g(end) < 0 && dt > v(end) / -g(end)
        dt = v(end) / -g(end);
    end
    s(end+1) = s(end) - v(end)*dt - .5*g(end)*dt^2 + v_lead * dt;
    v(end+1) = v(end) + g(end)*dt;
end
g(end+1) = NaN;

subplot(2,2,1)
plot(t, v, '-o'); xlabel('Time [s]'); ylabel('Speed [m/s]');
subplot(2,2,2)
plot(t, g, '-o'); xlabel('Time [s]'); ylabel('Acceleration [m/s^2]');
subplot(2,2,3)
plot(t(1:end-1), diff(t), '-o'); xlabel('Time [s]'); ylabel('Time step [s]');
subplot(2,2,4)
plot(t, s, '-o'); xlabel('Time [s]'); ylabel('Distance [m]');
text(0.0, 0.0, sprintf(' Minimum %.2fm', min(s)), 'VerticalAlignment', 'bottom');