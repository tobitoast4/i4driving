% This function reads rho.csv which can be produced by running 
% SocialPressureProxy from eclipse. As this can be a large file (>100MB) it
% is excluded from the repository.
%
% This function will show one vehicle after another. The only way to stop
% this is by clicking on the Command Window and typing Ctrl+C.
%
% Author: Wouter Schakel (w.j.schakel@tudelft.nl)
% Date: 26-06-2024

% Load data from .csv file
fid = fopen('rho.csv');
try
    t = textscan(fid, '%d %s %s %s %f %f %f %f %f %f %s', 'Delimiter', ',', ...
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

% Get unique GTUs, and order them by number (not alphabetically 1, 10, 100)
gtus = unique(t{4});
gtuNums = str2double(gtus);
[~, inds] = sort(gtuNums);
gtus = gtus(inds);

% Loop GTUs and show each, until user closes screen, then show next
for i = 1:length(gtus)
    these = strcmp(t{4}, gtus{i});
    x = t{6}(these);
    rho = t{9}(these);
    rho2 = t{10}(these);
    fig = figure;
    s1 = scatter(x, rho);
    hold on;
    s2 = scatter(x, rho2);
    title(['GTU ' gtus{i}]);
    legend([s1, s2], {'rho', 'rho2'});
    figure(fig);
    waitfor(fig);
end