% This function optimises the desired headway and normalizing deceleration
% used in the approximation of social pressure rho.
%
% This function reads rho.csv which can be produced by running 
% SocialPressureProxy from eclipse. As this can be a large file (>100MB) it
% is excluded from the repository.
%
% Author: Wouter Schakel (w.j.schakel@tudelft.nl)
% Date: 19-07-2024

% Load data from .csv file.
fid = fopen('rho.csv');
try
    header = textscan(fgetl(fid), '%s', 'Delimiter', ',');
    t = textscan(fid, '%d %s %s %s %f %f %f %f %f %f %f %f %f %s', 'Delimiter', ',', ...
        'EndOfLine', '\r\n', 'MultipleDelimsAsOne', true, 'HeaderLines', 1);
catch ex
    disp(['Unable to read data: ' ex.message]);
end
fclose(fid);

% Make x-coordinate absolute, not per section.
these = strcmp(t{2}, 'BC');
t{6}(these) = t{6}(these) + 1500;
these = strcmp(t{2}, 'CD');
t{6}(these) = t{6}(these) + 3000;

% Remove warm-up area
these = t{6} < 500;
for i = 1:length(t)
    t{i}(these) = [];
end

% Find columns.
rhoCol = strcmp(header{1}, 'rho');
sCol = strcmp(header{1}, 's');
v0leadCol = strcmp(header{1}, 'v0lead');
vLeadCol = strcmp(header{1}, 'vLead');
vCol = strcmp(header{1}, 'v');
aCol = strcmp(header{1}, 'a');
these = ~isnan(t{rhoCol}) & ~isnan(t{sCol}) & ~isnan(t{v0leadCol}) & ~isnan(t{vLeadCol});

% Get columns.
rho = t{rhoCol}(these);
s = t{sCol}(these);
v0lead = t{v0leadCol}(these);
vLead = t{vLeadCol}(these);
v = t{vCol}(these);
a = t{aCol}(these);

% Optimization.
fun = @(x) getRSquared(x, rho, s, v0lead, vLead, v, a);
x0 = [2.09]; % [T, bScale], or [bScale] to optimize with fixed T=1.6s
options = optimset('ObjectiveLimit', 0.001, 'TolX', 0.001, 'Display', 'iter', 'OutputFcn', @outfun);
x = fminsearch(fun, x0, options);

% This function determines the error as 1 - R-squared (as it is minimized).
function err = getRSquared(x, rho, s, v0lead, vLead, v, a)
    % Solve for 1 or 2 parameters.
    if length(x) > 1
        T = x(1);
        bScale = x(2);
    else
        T = 1.6;
        bScale = x(1);
    end

    % IDM+.
    ss = max(3 + v.*T + v.*(v-vLead) / (2.0 * sqrt(1.25*2.09)), 0);
    aLead = 1.25 * min(max(1-(v./v0lead).^4, -0.5), 1-(ss./s).^2);

    % Distance discount and approximate rho value.
    discount = 1;%1 - (s / 295);
    rhofit = max(0.0, min((a - aLead) / bScale, 1.0)) .* discount;
    
    % R-squared
    SStot = sum((rho-mean(rho)).^2);
    SSres = sum((rho-rhofit).^2);
    Rsq = 1-SSres/SStot;
    err = 1-Rsq; % we need to minimize error and maximize R-squared
end

% Displays current parameter values in the optimization algorithm.
function stop = outfun(x, ~, ~)
    stop = false;
    if length(x) > 1
        fprintf(' T=%.3fs, b=%.3fm/s2\n', x(1), x(2));
    else
        fprintf(' b=%.3fm/s2\n', x);
    end
end