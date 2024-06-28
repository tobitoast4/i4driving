function ManageResults(dataset,models,parameterSamplingType,setup)

    if strcmp(parameterSamplingType,'calibration')
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
        comparisonMap=[comparisonMap;comparisonMap+40;comparisonMap+80;comparisonMap+120];
        comparisonMap=[comparisonMap;comparisonMap+160;comparisonMap+320;comparisonMap+480];
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
                if length(intersect(comparisonMap(k,2),models))==2
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
    elseif strcmp(parameterSamplingType,'uniform') 
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
        [~,indexIn]=setdiff(labelsIn,labelsTest,'stable');
        parOut(indexIn)=0;
        clear labelsIn labelsTest
        clear indexIn indexTest
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
        mkdir(['Data/Models/model_',num2str(models(j))]);
        save(['Data/Models/model_',num2str(models(j)),'/filtered_',dataset,'.mat'],'parameters');
        clear parameters
    end
    clear j
    clear threshold
    clear inputs outputs
end