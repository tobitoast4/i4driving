% This function will show one vehicle after another. The only way to stop
% this is by clicking on the Command Window and typing Ctrl+C.
%
% This function will also generate figures showing the distribution of
% social pressure, both simulated and approximated.
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
    % check column numbers
    header = textscan(fgetl(fid), '%s', 'Delimiter', ',');
    rhoCol = strcmp(header{1}, 'rho');
    rho2Col = strcmp(header{1}, 'rho''');
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

% R-squared.
these = ~isnan(t{rhoCol}) & ~isnan(t{rho2Col});
y = t{rhoCol}(these);
yfit = t{rho2Col}(these);
SStot = sum((y-mean(y)).^2);
SSres = sum((y-yfit).^2);
Rsq = 1-SSres/SStot;
fprintf('R-squared: %.2f\n', Rsq);

% Histograms
rho = t{rhoCol}(these);
rho2 = t{rho2Col}(these);
edges = 0:0.02:1;
h = histcounts(rho, edges);
h = h / sum(h);
h2 = histcounts(rho2, edges);
h2 = h2 / sum(h2);
binCenters = (edges(1:end-1) + edges(2:end))/2;
subplot(2,1,1);
p = plot(binCenters, h);
hold on
p2 = plot(binCenters, h2);
legend([p, p2], {'{\rho}', '{\rho}'''});
xlabel('Social pressure')
ylabel('Distribution')
title('Distribution of social pressure')
% without extremes
subplot(2,1,2);
p = plot(binCenters(2:end-1), h(2:end-1));
hold on
p2 = plot(binCenters(2:end-1), h2(2:end-1));
legend([p, p2], {'{\rho}', '{\rho}'''});
xlabel('Social pressure')
ylabel('Distribution')
title('Distribution of social pressure (excluding extremes 0 and 1)')

% Cumulative distribution
figure;
p = plot(binCenters, cumsum(h));
hold on
p2 = plot(binCenters, cumsum(h2));
legend([p, p2], {'{\rho}', '{\rho}'''}, 'Location', 'southeast');
xlabel('Social pressure')
ylabel('Distribution')
title('Cumulative distribution of social pressure')

% Get unique GTUs, and order them by number (not alphabetically 1, 10, 100)
gtus = unique(t{4});
gtuNums = str2double(gtus);
[~, inds] = sort(gtuNums);
gtus = gtus(inds);

% Loop GTUs and show each, until user closes screen, then show next.
for i = 1:length(gtus)
    these = strcmp(t{4}, gtus{i});
    x = t{5}(these);
    rho = t{rhoCol}(these);
    rho2 = t{rho2Col}(these);
    fig = figure;
    s1 = scatter(x, rho);
    hold on;
    s2 = scatter(x, rho2);
    set(gca, 'YLim', [0 1]);
    title(['GTU ' gtus{i}]);
    legend([s1, s2], {'rho', 'rho2'});
    xlabel('Time [s]');
    ylabel('Social pressure [-]');
    figure(fig);
    waitfor(fig);
end