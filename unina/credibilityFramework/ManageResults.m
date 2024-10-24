function ManageResults(dataset,models,experiment,setup)

    if strcmp(experiment{1},'calibration')
        if setup.fixing==1
            comparisonMapBase=[
                1,1;
                2,1;
                3,2;
                4,2;
                5,3;
                5,4;
            ];
            comparisonMap=comparisonMapBase;
            comparisonMap=[comparisonMap;[[comparisonMapBase(:,1)+5,comparisonMapBase(:,1)];comparisonMapBase+5]];
            comparisonMap=[comparisonMap;[[comparisonMapBase(:,1)+10,comparisonMapBase(:,1)+5];comparisonMapBase+10]];
            comparisonMap=[comparisonMap;[[comparisonMapBase(:,1)+15,comparisonMapBase(:,1)+5];comparisonMapBase+15]];
            comparisonMap=[comparisonMap;[[21:40]',[1:20]'];comparisonMap+20];
            comparisonMap=[comparisonMap;[[comparisonMap(:,1)+40,comparisonMap(:,1)];comparisonMap+40;[comparisonMap(:,1)+80,comparisonMap(:,1)];comparisonMap+80;[comparisonMap(:,1)+120,comparisonMap(:,1)];comparisonMap+120]];
            comparisonMap=[comparisonMap;[comparisonMap+160;comparisonMap+320;comparisonMap+480;comparisonMap+640]];
            comparisonMap=unique(comparisonMap,'sorted','rows');

            parametersAll=cell(length(models),1);
            outputsAll=cell(length(models),1);
            for j = 1 : length(models)
                load(['Results/calibrationResults_',dataset,'_model_',num2str(models(j)),'.mat'],'parameters','outputs');
                parametersAll{j}=parameters;
                outputsAll{j}=outputs;
                clear parameters outputs
            end
            clear j
            parametersAllUpdated=parametersAll;
            outputsAllUpdated=outputsAll;
            numVehicles=size(parametersAllUpdated{1},1);
            for j = 1 : numVehicles
                for k = 1 : size(comparisonMap,1)
                    if length(intersect(comparisonMap(k,:),models))==2
                        indexModel=find(models==comparisonMap(k,1));
                        indexTest=find(models==comparisonMap(k,2));
                        [parametersAllUpdated{indexModel}(j,:),outputsAllUpdated{indexModel}(j,:)]=compareModels(...
                            parametersAllUpdated{indexModel}(j,:),...
                            outputsAllUpdated{indexModel}(j,:),...
                            comparisonMap(k,1),...
                            parametersAllUpdated{indexTest}(j,:),...
                            outputsAllUpdated{indexTest}(j,:),...
                            comparisonMap(k,2),...
                            setup.indexGOF.calibration...
                        );
                        clear indexModel indexTest
                    end
                end
                clear k
            end
            clear j
            clear parametersAll outputsAll numVehicles
            parameters=parametersAllUpdated;
            outputs=outputsAllUpdated;
            clear parametersAllUpdated outputsAllUpdated
            save(['Results/calibrationResults_',dataset,'.mat'],'parameters','outputs','-v7.3');
            clear parameters outputs
        end
    elseif strcmp(experiment{1},'uniform') 
        if setup.filtering==1
            MonteCarloFiltering(dataset,models,setup);
        end
    end
    
end

function [parOut,outputOut]=compareModels(parIn,outputIn,modelIn,parTest,outputTest,modelTest,indexGOF)
    if outputIn(indexGOF)>outputTest(indexGOF)
        outputOut=outputTest;
        labelsIn=GetParameterLabels(modelIn);
        labelsTest=GetParameterLabels(modelTest);
        [~,indexIn,indexTest]=intersect(labelsIn,labelsTest,'stable');
        parOut=parIn;
        parOut(indexIn)=parTest(indexTest);
        nominalParameters=getNominalParameters();
        [~,indexIn]=setdiff(labelsIn,labelsTest,'stable');
        for i = 1 : length(indexIn)
            parOut(indexIn(i))=nominalParameters.(labelsIn{indexIn(i)});
        end
        clear i
        clear labelsIn labelsTest
        clear indexIn indexTest
        clear nominalParameters
    else
        parOut=parIn;
        outputOut=outputIn;
    end
end

function MonteCarloFiltering(dataset,models,setup)
    if exist(['Results/calibrationResults_',dataset,'.mat'],'file')==0
        error('Calibration results on dataset %s are not available.\n',dataset);
    end
    load(['Results/calibrationResults_',dataset,'.mat'],'outputs');
    calOutputs=outputs;
    clear outputs
    % threshold=zeros(calOutputs,1);
    % for j = 1 : length(calOutputs)
    %     threshold(j,1)=max(threshold,max(calOutputs{j}(:,setup.indexGOF.calibration)));
    % end
    threshold=0;
    for j = 1 : length(calOutputs)
        threshold=max(threshold,max(calOutputs{j}(:,setup.indexGOF.calibration)));
    end
    clear j
    clear calOutputs
    for j = 1 : length(models)
        if exist(['Results/propagationResults_',dataset,'_model_', num2str(models(j)),'_uniform.mat'],'file')==0
            error('Propagation results (uniform) of model %li on dataset %s are not available.\n',models(j),dataset);
        end
        load(['Results/propagationResults_',dataset,'_model_', num2str(models(j)),'_uniform.mat'],'inputs','outputs');
        parameters=inputs(find(outputs.calibration(:,setup.indexGOF.calibration)<=threshold*setup.thresholdMultiplier),2:end);
        % for k = 1 : length(outputs.calibration(:,setup.indexGOF.calibration))
        %     parameters=inputs(find(outputs.calibration(:,setup.indexGOF.calibration)<=threshold(k,1)*setup.thresholdMultiplier),2:end);
        % end
        mkdir(['Data/Models/model_',num2str(models(j))]);
        save(['Data/Models/model_',num2str(models(j)),'/filtered_',dataset,'.mat'],'parameters');
        clear parameters
    end
    clear j
    clear threshold
    clear inputs outputs
end

function nominalParameters=getNominalParameters()
    nominalParameters.a=NaN;
    nominalParameters.b=NaN;
    nominalParameters.s0=NaN;
    nominalParameters.T=NaN;
    nominalParameters.deltaV0=NaN;
    nominalParameters.delta=NaN;
    nominalParameters.vCritPerc=0;
    nominalParameters.tp=0;
    nominalParameters.ta=0;
    nominalParameters.tpSA=0;
    nominalParameters.muT_s=0;
    nominalParameters.muT_v=0;
    nominalParameters.muX_1=1;
    nominalParameters.muX_2=0.5;
    nominalParameters.eps_s=1;
    nominalParameters.eps_v=1;
    nominalParameters.eps_dv=1;
    nominalParameters.epsSA=0;
    nominalParameters.epsTau=Inf;
    nominalParameters.epsW_s=0;
    nominalParameters.epsW_v=0;
    nominalParameters.epsW_dv=0;
    nominalParameters.hExp=0;
    nominalParameters.TC=0;
    nominalParameters.TScrit=0;
    nominalParameters.deltaTSmax=0;
    nominalParameters.deltaSAmax=0;
    nominalParameters.betaT=0;
    nominalParameters.gammaTD=0;
    nominalParameters.betaTD=0;
    nominalParameters.deltaTmax=0;
    nominalParameters.Tprob=0;
    nominalParameters.TminHigh=0;
    nominalParameters.deltaTmaxHigh=0;
    nominalParameters.TprobHigh=0;
    nominalParameters.deltaTcomf=0;
end