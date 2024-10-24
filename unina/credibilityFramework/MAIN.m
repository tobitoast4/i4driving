%% MAIN

clc
clear all
close all force
delete(gcp('nocreate'));
cluster = parcluster(parallel.defaultClusterProfile);
delete(cluster.Jobs);
clear cluster
warning('off');


%% Setup

% Datasets
% NGSIM
% HIGHD
% ZEN
datasets={'HIGHD'}; % {'NGSIM','HIGHD','ZEN'}

% Models
% 1) Base models: 5
%     - [1-160]     IDM (idm, Treiber et al., 2000)
%     - [161-320]   IDM+ (idm+, Schakel et al., 2012, TRR)
%     - [321-480]   I-IDM (iidm, Treiber and Kesting, 2013, book)
%     - [481-640]   I-IDM+ (iidm+, Tian et al., 2016, TR-F)
%     - [641-800]   M-IDM (midm, based on Treiber et al., 2000 and Tian et al., 2016, TR-F)
% 2) Cognitive model: 4
%     - [1-40]      none
%     - [41-80]     TD (Saifuzzaman and Zheng, 2015, TR-B)
%     - [81-120]    FDTD (van Lint and Calvert, 2018, TR-B)
%     - [121-160]   2D-IDM+ (Tian et al., 2016, TR-F, 2022, TR-C)
% 3) Spatial anticipation model: 2
%     - [1-20]      Single leader
%     - [21-40]     Multiple leaders (Ngoduy, 2015, Commun Nonlinear Sci Numer Simulat)
% 4) Perception error model: 4
%     - [1-5]       none
%     - [6-10]      fixed HDM (Treiber et al., 2006, Physica A)
%     - [11-15]     fixed + variable FDTD (van Lint and Calvert, 2018, TR-B)
%     - [16-20]     fixed + variable HDM (Treiber et al., 2006, Physica A)
% 5) Perception delay model: 3
%     - [1]         none
%     - [2-3]       fixed
%     - [4-5]       fixed + variable FDTD (van Lint and Calvert, 2018, TR-B)
% 6) Temporal anticipation model (only with perception delay): 2
%     - [1]         none
%     - [2]         fixed HDM (Treiber et al., 2006, Physica A)
models=[341:360, 381:400, 421:440, 461:480];

% Experiments
% Each cell contains:
% - parameters sampling type 
%   - calibration
%   - uniform
%   - empirical_marginal
%   - empirical_joint
%   - filtered_marginal
%   - filtered_joint
% - trajectory sampling type
%   - trajFixed
%   - trajFactor
% - trajectory-parameters sampling type (only if filtered_* and trajFactor)
%   - uncorr
%   - corr

% {{'calibration','trajFixed'}}
% {{'uniform','trajFixed'}}
% {{'uniform','trajFactor'}}
% {{'empirical_marginal','trajFactor'}}
% {{'empirical_joint','trajFactor'}}
% {{'filtered_marginal','trajFixed'}}
% {{'filtered_marginal','trajFactor','uncorr'}}
% {{'filtered_marginal','trajFactor','corr'}}
% {{'filtered_joint','trajFixed'}}
% {{'filtered_joint','trajFactor','uncorr'}}
% {{'filtered_joint','trajFactor','corr'}}

experiments=[...
    {{'calibration','trajFixed'}},...
];

% Labels
% 'ta','\tau_{a}'...
setup.textInputs=struct(...
    'trajID','trajID',...
    'a','a',...
    'b','b',...
    's0','s_{0}',...
    'T','T',...
    'deltaV0','\DeltaV_{0}',...
    'delta','\delta',...
    'vCritPerc','v_{crit,perc}',...
    'tp','\tau_{p}',...
    'tpSA','\tau_{p,SA}',...
    'muT_s','\mu_{T,s]',...
    'muT_v','\mu_{T,v]',...
    'muX_1','\mu_{X,1}',...
    'muX_2','\mu_{X,2}',...
    'eps_s',{[char(949),'_{s}']},...
    'eps_v',{[char(949),'_{v}']},...
    'eps_dv',{[char(949),'_{dv}']},...
    'epsSA',{[char(949),'_{SA}']},...
    'epsTau',{[char(949),'_{\tau}']},...
    'epsW_s',{[char(949),'_{Ws}']},...
    'epsW_v',{[char(949),'_{Wv}']},...
    'epsW_dv',{[char(949),'_{Wdv}']},...
    'hExp','h_{exp}',...
    'TC','TC',...
    'TScrit','TS_{crit}',...
    'deltaTSmax','\DeltaTS_{max}',...
    'deltaSAmax','\DeltaSA_{max}',...
    'betaT','\beta_{T}',...
    'gammaTD','\gamma_{TD}',...
    'betaTD','\beta_{TD}',...
    'deltaTmax','\DeltaT_{max}',...
    'Tprob','T_{prob}',...
    'TminHigh','T_{min,high}',......
    'deltaTmaxHigh','\DeltaT_{max,high}',......
    'TprobHigh','T_{prob,high}',...
    'deltaTcomf','\DeltaT_{comf}'...
);
setup.textOutputs={...
    'rmse(s)',...
    'rmse(v)',...
    'nrmse(s,v)',...
    'minNetSpacing',...
    'vFollClosest',...
    'vLeadClosest',...
    'xFollTot',...
};

% Setup analysis
setup.calibration=1;
setup.fixing=1;
setup.propagation=0;
setup.filtering=0;
setup.GSA=0;

% Replication
setup.numReplications=10;

% Design of Experiment
setup.numIterations=2^18;

% Multithreading
setup.numCPUs=28;

% Trajectory resampling
setup.resamplingRate=0.01; % NaN, value

% GOFs
% 1: rmse_s, 2: rmse_v, 3: nrmse_s_v, 4: minNetSpacing
setup.indexGOF.calibration=3;
setup.indexGOF.safety=4;
setup.percentileGOF.calibration=0.85;
setup.percentileGOF.safety=0;

% Monte Carlo filtering
setup.thresholdMultiplier=1.05;

% GSA
setup.indexOutputs=[3];
setup.outputThresholds=ones(length(setup.textOutputs),1)*10000;
setup.outputThresholdsFigure=ones(length(setup.textOutputs),1)*NaN;
setup.outputThresholdsFigure(3)=1;
setup.Sformula='SALTELLI'; % SALTELLI, JANSEN
setup.STformula='JANSEN'; % SALTELLI, JANSEN


%% Create Results folder

mkdir('Results/Figures');


%% Execution

for k = 1 : length(experiments)
    for i = 1 : length(datasets)
        for j = 1 : length(models)
            Execution(datasets{i},models(j),experiments{k},setup);
        end
        clear j
        % ManageResults(datasets{i},models,experiments{k},setup);
    end
    clear i
end
clear k

delete(gcp('nocreate'));