% This script shows a small interactive demo of the results of the ray
% marching algorithm to determine visibility from one cell to another.
%
% Author: Wouter Schakel (w.j.schakel@tudelft.nl)
% Date: 23-06-2024

% Input.
width = 100; % world width
height = 75; % world height
ds = 2; % cell size
objects = 10; % number of objects
minRadius = 2; % minimum object radius
maxRadius = 10; % maximum object radius
range = 30.5; % max visibility range
cutoff = 0.01; % object hit distance

% Cell coordinates and visibility to other cells.
w = ceil(width/ds);
h = ceil(height/ds);
cells = zeros(h,w,2);
cellVisibility = false(h,w,h,w);
for i = 1:h
    for j = 1:w
        cells(i,j,1) = (j-0.5) * ds;
        cells(i,j,2) = (i-0.5) * ds;
    end
end

% Object coordinates and radii.
world = zeros(objects,3);
for i = 1:objects
    world(i,1) = rand()*width;
    world(i,2) = rand()*height;
    world(i,3) = minRadius + rand()*(maxRadius - minRadius);
end

% Compute visibility.
of = num2str(h*w);
dCell = ceil(range/ds);
for i = 1:h
    disp(['progress cell ' num2str((i-1)*w + 0) ' of ' of '.']);
    for j = 1:w
        for m = max(1,i-dCell):min(h,i+dCell)
            for n = max(1,j-dCell):min(w,j+dCell)
                cellVisibility(i,j,m,n) = isVisible(cells(i,j,1), cells(i,j,2), ...
                    cells(m,n,1), cells(m,n,2), range, cutoff, world);
            end
        end
    end
end

% Visibility surface.
x = 0:ds:width;
y = 0:ds:height;
p = surface(x, y, zeros(length(y), length(x)), ones(length(y), length(x), 3), ...
    'FaceColor', 'flat', 'HitTest', 'off');

% World objects.
r = 0:pi/180:2*pi;
for i = 1:size(world,1)
    x = world(i,1) + sin(r)*world(i,3);
    y = world(i,2) + cos(r)*world(i,3);
    z = ones(size(x));
    patch(x, y, z, [1 1 1], 'LineStyle', 'none', 'FaceColor', [0 0 0], 'HitTest', 'off');
end

% Interactivity.
disableDefaultInteractivity(gca);
text(width/2, height, 'Click on the world or use arrow keys', ...
    'VerticalAlignment', 'bottom', 'HorizontalAlignment', 'center', 'HitTest', 'off');
set(gca, 'ToolBar', [], 'DataAspectRatio', [1 1 1], 'Visible', 'off', ...
    'XLim', [0 width], 'YLim', [0 height]);
set(gcf, 'ButtonDownFcn', {@(s, e, cellVisibility, p, ax, width, height, ds, w, h) ...
    clicked(s, e, cellVisibility, p, ax, width, height, ds, w, h), ...
    cellVisibility, p, gca, width, height, ds, w, h}); 
set(gcf, 'KeyPressFcn', {@(s, e, cellVisibility, p, ax, w, h) ...
    typed(s, e, cellVisibility, p, ax, w, h), ...
    cellVisibility, p, gca, w, h});
set(gcf, 'ToolBar', 'none', 'MenuBar', 'none');

% Determines whether (x1, y1) is visible from (x0, y0) based on the world.
% This uses the ray marching algorithm.
function visible = isVisible(x0, y0, x1, y1, range, cutoff, world)
    % Check range.
    d = sqrt((x1-x0)^2 + (y1-y0)^2);
    if d > range
        visible = false;
        return;
    end
    
    % Ray marching.
    angle = atan2(y1-y0, x1-x0);
    cosAngle = cos(angle);
    sinAngle = sin(angle);
    x = x0;
    y = y0;
    for i = 1:1000
        dObj = sqrt((world(:,1)-x).^2 + (world(:,2)-y).^2) - world(:,3);
        dMin = min(dObj);
        if dMin > d
            % Closest object further than target coordinate.
            visible = true;
            return;
        end
        if dMin < cutoff
            % Object hit.
            visible = false;
            return;
        end
        x = x + cosAngle*dMin;
        y = y + sinAngle*dMin;
        d = sqrt((x1-x)^2 + (y1-y)^2);
    end
    error('Maximum number of marching steps reached.')
end

% Called when mouse is clicked. Moves the selected cell by clicked point.
function clicked(~, ~, cellVisibility, p, ax, width, height, ds, w, h)
    point = get(ax, 'currentpoint');
    if point(1,1) > 0 && point(1,1) <= width && point(1,2) > 0 && point(1,2) <= height
        ci = ceil(point(1,2)/ds);
        cj = ceil(point(1,1)/ds);
        showVisibility(ci, cj, cellVisibility, p, w, h);
        set(ax, 'UserData', [ci, cj]);
    else
        clearVisibility(p, w, h);
        set(ax, 'UserData', []);
    end
end

% Called when a key is pressed. Moves the selected cell by arrow keys.
function typed(~, event, cellVisibility, p, ax, w, h)
    inds = get(ax, 'UserData');
    if isempty(inds)
        inds = floor([h/2, w/2]);
    end
    switch event.Key
        case 'leftarrow'
            inds(2) = max(1, inds(2) - 1);
        case 'rightarrow'
            inds(2) = min(w, inds(2) + 1);
        case 'downarrow'
            inds(1) = max(1, inds(1) - 1);
        case 'uparrow'
            inds(1) = min(h, inds(1) + 1);
        otherwise
            return;
    end
    set(ax, 'UserData', inds);
    showVisibility(inds(1), inds(2), cellVisibility, p, w, h);
end

% Clears all visibility.
function clearVisibility(p, w, h)
    set(p, 'CData', ones(h,w,3));
end

% Shows the visibility of the selected cell.
function showVisibility(ci, cj, cellVisibility, p, w, h)
    c = ones(h,w,3) * .5;
    v = repmat(squeeze(cellVisibility(ci,cj,:,:)), [1 1 3]);
    c(v) = 1;
    c(ci,cj,:) = [1 0 0];
    set(p, 'CData', c);
end