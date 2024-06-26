% This script calculates the attention matrix given an array of task demand
% values. From this, it will determine the task saturation, steady-state,
% anticipation reliance and perception delay. The results are printed to
% the Command Window.
%
% Author: Wouter Schakel (w.j.schakel@tudelft.nl)
% Date: 26-06-2024

% Input.
TD = [.9 .6 .4]; % task demand values of different tasks/areas
TC = 1.0; % task capacity
tauMax = 1.0; % maximum reaction time (in unit of choice)

% Input checks.
if (any(TD>=1 | TD<0))
    error('Task demand values should be in range [0...1).')
end

% Fill the transition matrix.
n =  length(TD);
sumTD = sum(TD);
P = zeros(n);
for i = 1:n
    for j = 1:n
        if (i==j)
            % monotasking: probability of staying on a task is its TD
            % for TD approaching 1, this creates attention tunneling
            P(i,i) = TD(i);
        else
            % if driver switches, another task is selection by ratio of TD
            P(i,j) = (1-TD(i)) * TD(j) / (sumTD-TD(i));
        end
    end
end

% Get the steady state.
if (sumTD > 0)
    [~, D, W] = eig(P, eye(n));
    % eigenvalue might not be exactly 1, find nearest
    dEigAnd1 = abs(diag(D) - 1);
    S = W(:,dEigAnd1 == min(dEigAnd1));
    S = S / sum(S); % normalize
else
    S = ones(n,1) / n;
end

% Derive AR.
AR = max(0, TD(:) - S(:));
ARratio = AR ./ TD(:);
ARratio(TD(:)==0) = 1;
tau = (tauMax / TC)*ARratio;
TS = max(0, sumTD/TC - 1);
if (sumTD==0); Smulti = ones(n,1)/n; else; Smulti = TD(:)/sumTD; end
ARmulti = max(0, TD(:) - Smulti);
if (sumTD==0); tauMulti=tauMax; else; tauMulti = (tauMax / TC) * (sum(ARmulti)/sumTD); end  

% Display results.
clc;
blanks = '   ';
format = ['%.3f' blanks];
disp(['TD:  ' blanks num2str(TD(:)', format) blanks '(task demand)'])
disp(['TS:  ' blanks num2str(TS, format)])
disp('=Serial monotasker=')
disp(['AR:  ' blanks num2str(AR', format) blanks '(anticipation reliance)'])
disp(['S:   ' blanks num2str(S', format) blanks '(steady state, or attention)'])
disp(['tau: ' blanks num2str(tau', format) blanks '(perception delay)'])
disp('')
disp('=Multitasker=')
disp(['AR*: ' blanks num2str(ARmulti', format)])
disp(['S*:  ' blanks num2str(Smulti', format)])
disp(['tau*:' blanks num2str(tauMulti, format)])