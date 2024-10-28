function Execution(dataset,model,experiment,setup)

    % Create Results folder
    mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1}]);
    
    % Loading data
    dataVehicles=loadingData(dataset,setup.resamplingRate);
    
    % Parameter labels
    labels=GetParameterLabels(model);
    
    % Parameter bounds
    bounds=ParseBounds(labels);
    LB=[0.5,bounds(1,:)];
    UB=[length(dataVehicles)+0.5,bounds(2,:)];
    clear bounds
    
    % Calibration
    if strcmp(experiment{1},'calibration') && setup.calibration==1
        Calibration(dataset,model,setup,dataVehicles,LB(2:end),UB(2:end),labels);
    end
    
    if strcmp(experiment{1},'calibration')
        return;
    end
    
    % Uncertainty propagation
    if setup.propagation==1
        Propagation(dataset,model,experiment,setup,dataVehicles,LB,UB,labels);
    end
    
    % Global Sensitivity Analysis
    if setup.GSA==1
        GSA(dataset,model,experiment,setup,LB,UB,labels);
    end
    
end

function Calibration(dataset,model,setup,dataVehicles,LB,UB,labels)

    mkdir(['Results/',dataset,'/model_',num2str(model),'/calibration/vehicles']);
    
    numCPUs=setup.numCPUs;
    numReplications=setup.numReplications;
    indexGOF=setup.indexGOF.calibration;
    percentileGOF=setup.percentileGOF.calibration;
    numVehicles=length(dataVehicles);

    function UpdateWaitbar(iteration)
        fprintf('dataset: %s, model: %li, calibration count %li/%li (vehicle_%li): completed.\n',dataset,model,p,numVehicles,iteration);
        waitbar(p/numVehicles, h);
        p = p + 1;
    end

    if isempty(gcp('nocreate'))
        parpool(parallel.defaultClusterProfile,numCPUs);
    end
    D = parallel.pool.DataQueue;
    afterEach(D, @UpdateWaitbar);
    h = waitbar(0, 'Calibrating vehicles: please wait...');
    p = 1;
    parfor iteration = 1 : numVehicles
        processCalibration(D,iteration,dataset,model,dataVehicles{iteration},LB,UB,labels,numReplications,indexGOF,percentileGOF);
    end
    clear i
    close(h);
    clear h
    
    parametersAll=ones(numVehicles,length(LB))*NaN;
    outputsAll=ones(numVehicles,length(setup.textOutputs))*NaN;
    for iteration = 1 : numVehicles
        load(['Results/',dataset,'/model_',num2str(model),'/calibration/vehicles/vehicle_',num2str(iteration),'.mat'],'parameters','outputs');
        parametersAll(iteration,:)=parameters;
        outputsAll(iteration,:)=outputs;
        clear parameters outputs
    end
    clear i
    
%     rmdir(['Results/',dataset,'/model_',num2str(model),'/calibration/vehicles'],'s');
    
    parameters=parametersAll;
    outputs=outputsAll;
    clear parametersAll outputsAll
    
    save(['Results/calibrationResults_',dataset,'_model_',num2str(model),'.mat'],'parameters','outputs','labels');

end

function Propagation(dataset,model,experiment,setup,dataVehicles,LB,UB,labels)
    
    if strcmp(experiment{2},'trajFixed')
        for i = 1 : length(dataVehicles)
            [inputs,outputs]=processPropagation(dataset,model,experiment,setup,dataVehicles,LB,UB,labels,i);
            save(['Results/propagationResults_',dataset,'_model_',num2str(model),'_',experiment{1},'_',experiment{2},'_','vehicle_',num2str(i),'.mat'],'inputs','outputs','labels','-v7.3');
        end
        clear i
    else
        [inputs,outputs]=processPropagation(dataset,model,experiment,setup,dataVehicles,LB,UB,labels,NaN);
        if strcmp(experiment{1},'filtered_marginal') || strcmp(experiment{1},'filtered_joint')
            tag=['_',experiment{3}];
        else
            tag='';
        end
        save(['Results/propagationResults_',dataset,'_model_',num2str(model),'_',experiment{1},'_',experiment{2},tag,'.mat'],'inputs','outputs','labels','-v7.3');
    end
    
end

function GSA(dataset,model,experiment,setup,LB,UB,labels)
    
    if strcmp(experiment{2},'trajFixed')
        for i = 1 : length(dataVehicles)
            load(['Results/propagationResults_',dataset,'_model_', num2str(model),'_',experiment{1},'_',experiment{2},'_','vehicle_',num2str(i),'.mat'],'inputs','outputs');
            [indices,bounds,textInputs,indexInputs]=processGSA(dataset,model,experiment,setup,LB(2:end),UB(2:end),labels,inputs(:,2:end),outputs.calibration,i);
            clear inputs outputs
            save(['Results/gsaResults_',dataset,'_model_',num2str(model),'_',experiment{1},'_','vehicle_',num2str(i),'.mat'],'indices','bounds','textInputs','indexInputs');
        end
        clear i
    else
        if strcmp(experiment{1},'filtered_marginal') || strcmp(experiment{1},'filtered_joint')
            tag=['_',experiment{3}];
        else
            tag='';
        end
        load(['Results/propagationResults_',dataset,'_model_', num2str(model),'_',experiment{1},'_',experiment{2},tag,'.mat'],'inputs','outputs');
        [indices,bounds,textInputs,indexInputs]=processGSA(dataset,model,experiment,setup,LB,UB,labels,inputs,outputs.calibration,NaN);
        clear inputs outputs
        save(['Results/gsaResults_',dataset,'_model_',num2str(model),'_',experiment{1},'_',experiment{2},tag,'.mat'],'indices','bounds','textInputs','indexInputs');
    end
    
end

function processCalibration(D,iteration,dataset,model,dataVehicle,LB,UB,labels,numReplications,indexGOF,percentileGOF)
    if exist(['Results/',dataset,'/model_',num2str(model),'/calibration/vehicles/vehicle_',num2str(iteration),'.mat'],'file')>0
        send(D,iteration);
        return;
    end
    parameters=ga(...
        @(x)getObjFunction(x,labels,model,dataVehicle,numReplications,indexGOF,percentileGOF),...
        length(LB),...
        [],[],[],[],...
        LB,UB,...
        [],...
        gaoptimset(...
            'PopulationSize',1000,... %1000
            'Generations',10000,... %10000
            'StallGenLimit',100,...
            'TolFun',1e-4,...
            'UseParallel',false,...
            'display','off')...
        );
    [outputsReps,dataVehicleReps]=Simulation(parameters,labels,model,dataVehicle,numReplications);
    [outputs,dataVehicle]=getBestReplication(outputsReps,dataVehicleReps,indexGOF,percentileGOF);
    save(['Results/',dataset,'/model_', num2str(model),'/calibration/vehicles/vehicle_',num2str(iteration),'.mat'],'parameters','outputs','dataVehicle');
    send(D,iteration);
end

function [inputs,outputs]=processPropagation(dataset,model,experiment,setup,dataVehicles,LB,UB,labels,vehicleIndex)

    numIterations=setup.numIterations;
    numReplications=setup.numReplications;
    numCPUs=setup.numCPUs;
    numOutputs=length(setup.textOutputs);
    numInputs=length(LB);
    
    if strcmp(experiment{2},'trajFixed')
        mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},'/vehicles/vehicle_',num2str(vehicleIndex),'/iterations']);
    else
        if strcmp(experiment{1},'filtered_marginal') || strcmp(experiment{1},'filtered_joint')
            tag=['_',experiment{3}];
        else
            tag='';
        end
        mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/iterations']);
    end
    
    if strcmp(experiment{1},'empirical_marginal') || ...
            strcmp(experiment{1},'empirical_joint') ||...
            strcmp(experiment{1},'filtered_marginal') ||...
            strcmp(experiment{1},'filtered_joint')
        if strcmp(experiment{1},'empirical_marginal') ||...
                strcmp(experiment{1},'empirical_joint')
            if strcmp(experiment{2},'trajFixed')
                error('Only one parameter sample per trajectory exists (i.e., the calibrated values). Please use experiment{2} equal to ''trajFactor''.');
            end
            if exist(['Results/calibrationResults_',dataset,'.mat'],'file')==0
                error('Empirical parameters distribution from calibration not found (dataset %s).\n',dataset);
            else
                load(['Results/calibrationResults_',dataset,'.mat'],'parameters');
                if isempty(parameters{model})
                    error('Empirical parameters distribution from calibration not found (dataset %s, model %li).\n',dataset,model);
                end
                inputs=[[1:size(parameters{model},1)]',parameters{model}];
                clear parameters
            end
        else
            if strcmp(experiment{2},'trajFixed')
                vehicleTag=['_vehicle_',num2str(vehicleIndex)];
            else
                vehicleTag='';
            end
            if exist(['Results/propagationResultsFiltered_',dataset,'_model_',num2str(model),'_','uniform',vehicleTag,'.mat'],'file')==0
                error('Filtered parameters distribution from propagation not found (dataset %s, model %li).\n',dataset,model);
            else
                load(['Results/propagationResultsFiltered_',dataset,'_model_',num2str(model),'_','uniform',vehicleTag,'.mat'],'inputs');
            end
            clear vehicleTag
        end
        if strcmp(experiment{1},'empirical_joint') ||...
                strcmp(experiment{1},'filtered_joint')
            if strcmp(experiment{1},'empirical_joint') || strcmp(experiment{3},'uncorr')
                rData=corr(inputs(:,2:end));
                indexNaN=all(isnan(rData),1);
                rData(:,indexNaN)=0;
                rData(indexNaN,:)=0;
                for i = 1 : size(rData,1)
                    rData(i,i)=1;
                end
                clear i
                clear indexNaN
                for threshold = 1: -0.01 : 0.5
                    r=rData;
                    [indexRow,indexCol]=find(abs(r)>=threshold);
                    indexMat=[indexRow,indexCol];
                    index=find(diff(indexMat,'',2)~=0);
                    for j = 1 : length(index)
                        r(indexMat(index(j),1),indexMat(index(j),2))=threshold;
                    end
                    clear j
                    clear index indexMat indexRow indexCol
                    r=triu(r)+triu(r,1)';
                    cholMatrix=cholcov(r);
                    if ~isempty(cholMatrix) && size(cholMatrix,1)==size(cholMatrix,2)
                        break;
                    end
                    r=nearcorr(r);
                    r=triu(r)+triu(r,1)';
                    cholMatrix=cholcov(r);
                    if ~isempty(cholMatrix) && size(cholMatrix,1)==size(cholMatrix,2)
                        break;
                    end
                    clear r
                end
                clear rData threshold
            else
                trajectoryIDs=unique(inputs(:,1));
                cholMatrix=cell(length(trajectoryIDs),1);
                for i = 1 : length(trajectoryIDs)
                    rData=corr(inputs(inputs(:,1)==trajectoryIDs(i),2:end));
                    indexNaN=all(isnan(rData),1);
                    rData(:,indexNaN)=0;
                    rData(indexNaN,:)=0;
                    for j = 1 : size(rData,1)
                        rData(j,j)=1;
                    end
                    clear j
                    clear indexNaN;
                    cholMatrix=chol(rData);
                    clear rData
                end
                clear i
                clear trajectoryIDs
            end
        else
            cholMatrix=[];
        end
        if strcmp(experiment{1},'empirical_marginal') ||...
                strcmp(experiment{1},'empirical_joint') ||...
                strcmp(experiment{1},'filtered_marginal') ||...
                strcmp(experiment{3},'uncorr')
            marginals=zeros(size(inputs,1),size(inputs,2)-1);
            for i = 1 : size(inputs,2)-1
                marginals(:,i)=sortrows(inputs(:,i+1));
            end
            clear i
        else
            trajectoryIDs=unique(inputs(:,1));
            marginals=cell(length(trajectoryIDs),1);
            for j = 1 : length(trajectoryIDs)
                inputsLocal=inputs(inputs(:,1)==trajectoryIDs(i),:);
                marginals{j}=zeros(size(inputsLocal,1),size(inputsLocal,2)-1);
                for i = 1 : size(inputsLocal,2)
                    marginals{j}(:,i)=sortrows(inputsLocal(:,i+1));
                end
                clear i
                clear inputsLocal
            end
            clear j
            clear trajectoryIDs
        end
        clear inputs
    else
        marginals=[];
        cholMatrix=[];
    end
    
    function UpdateWaitbar(iteration)
        fprintf('dataset: %s, model: %li, experiment: %s, iteration count %li/%li (iter_%li): completed.\n',dataset,model,[experiment{1},'_',experiment{2},'_',num2str(vehicleIndex)],p,numIterations,iteration);
        waitbar(p/numIterations, h);
        p = p + 1;
    end

    if isempty(gcp('nocreate'))
        parpool(parallel.defaultClusterProfile,numCPUs);
    end
    D = parallel.pool.DataQueue;
    afterEach(D, @UpdateWaitbar);
    h = waitbar(0, 'Processing iterations: please wait...');
    p = 1;
    parfor iteration = 1 : numIterations
        processIteration(D,iteration,dataset,model,experiment,numInputs,dataVehicles,cholMatrix,marginals,LB,UB,labels,numReplications,vehicleIndex);
    end
    clear i
    close(h);
    clear h

    if strcmp(experiment{1},'uniform') ||...
            strcmp(experiment{1},'empirical_marginal') ||...
            strcmp(experiment{1},'filtered_marginal')
        numInputsRows=numInputs;
    else
        numInputsRows=2;
    end

    inputsAll=ones(numIterations*(numInputsRows+2),numInputs)*NaN;
    outputsAll.calibration=ones(numIterations*(numInputsRows+2),numOutputs)*NaN;
    outputsAll.safety=ones(numIterations*(numInputsRows+2),numOutputs)*NaN;
    for iteration = 1 : numIterations
        if strcmp(experiment{2},'trajFixed')
            load(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},'/vehicles/vehicle_',num2str(vehicleIndex),'/iterations/iter_',num2str(iteration),'.mat'],'inputs','outputs');
        else
            load(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/iterations/iter_',num2str(iteration),'.mat'],'inputs','outputs');
        end
        inputsAll((iteration-1)*(numInputsRows+2)+1:(iteration-1)*(numInputsRows+2)+numInputsRows+2,:)=inputs;
        count=0;
        for j = (iteration-1)*(numInputsRows+2)+1 : (iteration-1)*(numInputsRows+2)+numInputsRows+2
            count=count+1;
            outputsAll.calibration(j,:)=getBestReplication(outputs{count},cell(numReplications,1),setup.indexGOF.calibration,setup.percentileGOF.calibration);
            outputsAll.safety(j,:)=getBestReplication(outputs{count},cell(numReplications,1),setup.indexGOF.safety,setup.percentileGOF.safety);
        end
        clear j
        clear count
        clear inputs outputs
    end
    clear i
    
%     if strcmp(experiment{2},'trajFixed')
%         rmdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},'/vehicles/vehicle_',num2str(vehicleIndex),'/iterations'],'s');
%     else
%         rmdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/iterations'],'s');
%     end
    
    inputs=inputsAll;
    outputs=outputsAll;
    clear inputsAll outputsAll

end

function processIteration(D,iteration,dataset,model,experiment,numInputs,dataVehicles,cholMatrix,marginals,LB,UB,labels,numReplications,vehicleIndex)
    if strcmp(experiment{2},'trajFixed')
        if exist(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},'/vehicles/vehicle_',num2str(vehicleIndex),'/iterations/iter_',num2str(iteration),'.mat'],'file')>0
            send(D,iteration);
            return;
        end
        numInputs=numInputs-1;
    else
        if strcmp(experiment{1},'filtered_marginal') || strcmp(experiment{1},'filtered_joint')
            tag=['_',experiment{3}];
        else
            tag='';
        end
        if exist(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/iterations/iter_',num2str(iteration),'.mat'],'file')>0
            send(D,iteration);
            return;
        end
    end
    T=sobolseq6144(iteration,2*numInputs);
    A=T(1:numInputs);
    B=T(numInputs+1:2*numInputs);
    inputSet=[A;B];
    if strcmp(experiment{2},'trajFactor') &&...
            (strcmp(experiment{1},'empirical_joint') || strcmp(experiment{1},'filtered_joint'))
        for j = 1 : 2
            Ab=A;
            if j==1
                Ab(1,j)=B(1,j);
            else
                Ab(1,2:end)=B(1,2:end);
            end
            inputSet=[inputSet;Ab];
        end
        clear j
    else
        for j = 1 : numInputs
            Ab=A;
            Ab(1,j)=B(1,j);
            inputSet=[inputSet;Ab];
        end
        clear j
    end
    if strcmp(experiment{2},'trajFixed')
        inputSet=[ones(size(inputSet,1),1)*(vehicleIndex/(length(dataVehicles)+1)),inputSet];
    end
    inputs=zeros(size(inputSet));
    outputs=cell(size(inputSet,1),1);
    for j = 1 : size(inputSet,1)
        inputs(j,1)=round(LB(1)+inputSet(j,1)*(UB(1)-LB(1)));
        if iscell(marginals)
            marginalsLocal=marginals{inputs(j,1)};
        else
            marginalsLocal=marginals;
        end
        if iscell(cholMatrix)
            cholMatrixLocal=cholMatrix{inputs(j,1)};
        else
            cholMatrixLocal=cholMatrix;
        end
        if strcmp(experiment{1},'uniform') ||...
                strcmp(experiment{1},'empirical_marginal') ||...
                strcmp(experiment{1},'filtered_marginal')
            for k = 2 : size(inputSet,2)
                if strcmp(experiment{1},'uniform')
                    inputs(j,k)=LB(k)+inputSet(j,k)*(UB(k)-LB(k));
                elseif strcmp(experiment{1},'empirical_marginal') || strcmp(experiment{1},'filtered_marginal')
                    inputs(j,k)=marginalsLocal(ceil(size(marginalsLocal,1)*inputSet(j,k)),k-1);
                end
            end
            clear k
        else
            inputs(j,2:end)=copula(inputSet(j,2:end),marginalsLocal,cholMatrixLocal);
        end
        outputs{j}=Simulation(inputs(j,2:end),labels,model,dataVehicles{inputs(j,1)},numReplications);
        if max(isnan(outputs{j}),[],'all')==1
            error('Simulation error (propagation iteration=%li, sample=%li).',iteration,j);
        end
    end
    clear j
    if strcmp(experiment{2},'trajFixed')
        save(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},'/vehicles/vehicle_',num2str(vehicleIndex),'/iterations/iter_',num2str(iteration),'.mat'],'inputs','outputs');
    else
        save(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/iterations/iter_',num2str(iteration),'.mat'],'inputs','outputs');
    end
    send(D,iteration);
end

function [indices,bounds,textInputs,indexInputs]=processGSA(dataset,model,experiment,setup,LB,UB,labels,inputs,outputs,vehicleIndex)
    
    if strcmp(experiment{2},'trajFixed')
        vehicleTag=['vehicle_',num2str(vehicleIndex)];
        tag=['/vehicles/',vehicleTag];
    else
        vehicleTag='';
        if strcmp(experiment{1},'filtered_marginal') || strcmp(experiment{1},'filtered_joint')
            tag=['_',experiment{3}];
        else
            tag='';
        end
    end
    
    if strcmp(experiment{1},'uniform') || ...
            strcmp(experiment{1},'empirical_marginal') ||...
            strcmp(experiment,'filtered_marginal')
        textInputKeys=fieldnames(setup.textInputs);
        textInputs=cell(length(textInputKeys),1);
        for i = 1 : length(textInputKeys)
            textInputs{i}=setup.textInputs.(textInputKeys{i});
        end
        clear i
        if strcmp(experiment{2},'trajFixed')
            addFactor=[];
        else
            addFactor=1;
        end
        indexInputs=zeros(length(labels)+(isempty(addFactor)==0),1);
        indexInputs(addFactor)=1;
        for i = 1 : length(labels)
            indexInputs(i+(isempty(addFactor)==0))=find(strcmp(labels{i},textInputKeys)==1);
        end
        clear i
        clear textInputKeys
        clear addFactor
    else
        textInputs=[setup.textInputs(1),{'parameters'}];
        indexInputs=[1,2];
    end
    
    textOutputs=setup.textOutputs;
    indexOutputs=setup.indexOutputs;
    outputThresholds=setup.outputThresholds;
    outputThresholdsFigure=setup.outputThresholdsFigure;
    
    Sformula=setup.Sformula;
    STformula=setup.STformula;
    
    numInputs=length(indexInputs);
    numOutputs=length(textOutputs);
    numOutputsAnalysis=length(indexOutputs);
    numBlocks=30;
    numBootstrapReplicas=100;
    
    for j = 1 : numOutputsAnalysis
        mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)}]);
%         mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)},'/FIG']);
%         mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)},'/JPEG']);
%         mkdir(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)},'/EMF']);
    end
    clear j
    
    N=size(outputs,1)/(numInputs+2);

    Y_A=zeros(N,numOutputs);
    Y_B=zeros(N,numOutputs);
    Y_Ab=repmat(0,[N numInputs numOutputs]);
    for i = 1 : N
        Y_A(i,:)=outputs((i-1)*(numInputs+2)+1,:);
        Y_B(i,:)=outputs((i-1)*(numInputs+2)+2,:);
        Y_Ab(i,:,:)=outputs((i-1)*(numInputs+2)+3:(i-1)*(numInputs+2)+2+numInputs,:);
    end
    clear i
    
    indices=cell(numOutputsAnalysis,1);
    bounds=cell(numOutputsAnalysis,1);
    for j = 1 : numOutputsAnalysis
        if isnan(outputThresholds(indexOutputs(j)))
            NonReal_Indices_Y_A=find(Y_A(:,indexOutputs(j))==100000|Y_A(:,indexOutputs(j))==999999);
            NonReal_Indices_Y_B=find(Y_B(:,indexOutputs(j))==100000|Y_B(:,indexOutputs(j))==999999);
            NonReal_Indices_Local=unique(vertcat(NonReal_Indices_Y_A,NonReal_Indices_Y_B));
            for k = 1 : numInputs
                NonReal_Indices_Y_Ab=[];
                NonReal_Indices_Y_Ab=find(Y_Ab(:,k,indexOutputs(j))==100000|Y_Ab(:,k,indexOutputs(j))==999999);
                NonReal_Indices_Local=unique(vertcat(NonReal_Indices_Local,NonReal_Indices_Y_Ab));
            end
            clear k
            NonReal_Indices(:,1)=NonReal_Indices_Local;
            N_local=N-size(NonReal_Indices,1);
            clear NonReal_Indices_Y_A NonReal_Indices_Y_B NonReal_Indices_Y_Ab
            clear NonReal_Indices_Local
        else
            NonReal_Indices_Y_A=find(Y_A(:,indexOutputs(j))==100000|Y_A(:,indexOutputs(j))==999999|Y_A(:,indexOutputs(j))>outputThresholds(indexOutputs(j)));
            NonReal_Indices_Y_B=find(Y_B(:,indexOutputs(j))==100000|Y_B(:,indexOutputs(j))==999999|Y_B(:,indexOutputs(j))>outputThresholds(indexOutputs(j)));
            NonReal_Indices_Local=unique(vertcat(NonReal_Indices_Y_A,NonReal_Indices_Y_B));
            for k = 1 : numInputs
                NonReal_Indices_Y_Ab=[];
                NonReal_Indices_Y_Ab=find(Y_Ab(:,k,indexOutputs(j))==100000|Y_Ab(:,k,indexOutputs(j))==999999|Y_Ab(:,k,indexOutputs(j))>outputThresholds(indexOutputs(j)));
                NonReal_Indices_Local=unique(vertcat(NonReal_Indices_Local,NonReal_Indices_Y_Ab));
            end
            clear k
            NonReal_Indices(:,1)=NonReal_Indices_Local;
            N_local=N-size(NonReal_Indices,1);
            clear NonReal_Indices_Y_A NonReal_Indices_Y_B NonReal_Indices_Y_Ab
            clear NonReal_Indices_Local
        end

%         yA=zeros(N_local,1);
%         yB=zeros(N_local,1);
%         yAb=zeros(N_local,numInputs);
        yA=Y_A(:,indexOutputs(j));
        yB=Y_B(:,indexOutputs(j));
        yAb=Y_Ab(:,:,indexOutputs(j));
        yA(NonReal_Indices)=[];
        yB(NonReal_Indices)=[];
        yAb(NonReal_Indices,:)=[];
        clear NonReal_Indices
        
        Ji=zeros(N_local,numInputs);
        JTi=zeros(N_local,numInputs);
        for k = 1 : numInputs
            if strcmp(Sformula,'SALTELLI')==1
                Ji(:,k)=yB.*(yAb(:,k)-yA);
            elseif strcmp(Sformula,'JANSEN')==1
                Ji(:,k)=(yB-yAb(:,k)).^2;
            end
            if strcmp(STformula,'SALTELLI')==1
                JTi(:,k)=yA.*(yA-yAb(:,k));
            elseif strcmp(STformula,'JANSEN')==1
                JTi(:,k)=(yA-yAb(:,k)).^2;
            end
        end
        clear k
        
        Vtot=var([yA;yB]);
        if Vtot==0
            Vtot=1e-4;
        end
        clear yA yB yAb
        
        S=zeros(N_local,numInputs);
        ST=zeros(N_local,numInputs);
        if strcmp(Sformula,'SALTELLI')==1
            S=(cumsum(Ji)./[1:N_local]')/Vtot;
        elseif strcmp(Sformula,'JANSEN')==1
            S=1-0.5*(cumsum(Ji)./[1:N_local])/Vtot;
        end
        if strcmp(STformula,'SALTELLI')==1
            ST=(cumsum(JTi)./[1:N_local])/Vtot;
        elseif strcmp(STformula,'JANSEN')==1
            ST=0.5*(cumsum(JTi)./[1:N_local]')/Vtot;
        end
        
        numBlocks=min(N_local,numBlocks);
        
        N_localBlock=floor(N_local/numBlocks);
        S_LB=zeros(numBlocks,numInputs);
        S_UB=zeros(numBlocks,numInputs);
        ST_LB=zeros(numBlocks,numInputs);
        ST_UB=zeros(numBlocks,numInputs);
        for i = 1 : numBlocks
            nRowsIndicesMatrix=N_localBlock*i;
            IndicesMatrix=floor(rand(nRowsIndicesMatrix,numBootstrapReplicas)*nRowsIndicesMatrix+1);
            S_localBlock=zeros(numBootstrapReplicas,numInputs);
            ST_localBlock=zeros(numBootstrapReplicas,numInputs);
            for b = 1 : numBootstrapReplicas
                for k =1 : numInputs
                    Ji_localBlock(:,1)=Ji(IndicesMatrix(:,b),k);
                    JTi_localBlock(:,1)=JTi(IndicesMatrix(:,b),k);
                    if strcmp(Sformula,'SALTELLI')==1
                        S_localBlock(b,k)=mean(Ji_localBlock)/Vtot;
                    elseif strcmp(Sformula,'JANSEN')==1
                        S_localBlock(b,k)=1-0.5*mean(Ji_localBlock)/Vtot;
                    end
                    if strcmp(STformula,'SALTELLI')==1
                        ST_localBlock(b,k)=mean(JTi_localBlock)/Vtot;
                    elseif strcmp(STformula,'JANSEN')==1
                        ST_localBlock(b,k)=0.5*mean(JTi_localBlock)/Vtot;
                    end
                end
                clear k
                clear Ji_localBlock JTi_localBlock
            end
            clear b
            for k = 1 : numInputs
                S_sorted=sort(S_localBlock(:,k));
                S_LB(i,k)=S_sorted(round(0.05*numBootstrapReplicas),1);
                S_UB(i,k)=S_sorted(round(0.95*numBootstrapReplicas),1);
                ST_sorted=sort(ST_localBlock(:,k));
                ST_LB(i,k)=ST_sorted(round(0.05*numBootstrapReplicas),1);
                ST_UB(i,k)=ST_sorted(round(0.95*numBootstrapReplicas),1);
            end
            clear k
            clear S_sorted ST_sorted
            clear S_localBlock ST_localBlock
            clear IndicesMatrix nRowsIndicesMatrix
        end
        clear i
        clear Ji JTi Vtot

        StableS=[S(end,:)';sum(S(end,:))];
        StableST=[ST(end,:)';sum(ST(end,:))];
        StableS_bounds=[S_LB(end,:)',S_UB(end,:)'];
        StableST_bounds=[ST_LB(end,:)',ST_UB(end,:)'];

        % ST_Figure=max(0,StableST(1:end-1));
        % ST_LB_Figure=max(0,ST_LB(end,:))';
        % ST_UB_Figure=max(0,ST_UB(end,:))';
        % S_Figure=max(0,StableS(1:end-1));
        % S_LB_Figure=max(0,S_LB(end,:))';
        % S_UB_Figure=max(0,S_UB(end,:))';
        % 
%         Figure=figure('InvertHardcopy','off','Color',[1 1 1],'Units','centimeters','Position',[3 8 35 12],'paperposition',[0 0 35 12]);
%         h=bar(ST_Figure);
%         set(h(1),'EdgeColor','r','FaceColor','none')
%         hold on
%         h=bar(S_Figure);
%         set(h(1),'EdgeColor','b','FaceColor','none')
%         h=highlow(ST_Figure,ST_UB_Figure,ST_LB_Figure,ST_Figure);
%         set(h(1),'LineStyle','-','LineWidth',1.2,'Color','r')
%         set(h(1),'Marker','s')
%         set(h(1),'MarkerSize',2.5)
%         set(h(1),'MarkerFaceColor','r')
%         h=highlow(S_Figure,S_UB_Figure,S_LB_Figure,S_Figure);
%         set(h(1),'LineStyle','-','LineWidth',1.2,'Color','b')
%         set(h(1),'Marker','s')
%         set(h(1),'MarkerSize',2.5)
%         set(h(1),'MarkerFaceColor','b')
%         set(gca,'XTickLabel',textInputs);
%         title(sprintf('First-order and Total Sensitivity Indices - %s',textOutputs{indexOutputs(j)}),'FontWeight','bold')
%         legend('Total Sensitivity Index (ST)','First Order Sensitivity Index (S)','ST: 90% confidence interval','S: 90% confidence interval')
%         axis([0 numInputs+1 0 1.5])
%         clear Title
%         DestinationFolder=['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)}];
%         saveas(Figure,[DestinationFolder,'/FIG/Indices.fig']);
%         saveas(Figure,[DestinationFolder,'/JPEG/Indices.jpeg']);
%         saveas(Figure,[DestinationFolder,'/EMF/Indices.emf']);
%         close(Figure);
%         clear Figure DestinationFolder
%         clear S_Figure S_LB_Figure S_UB_Figure
%         clear ST_Figure ST_LB_Figure ST_UB_Figure
%         clear h
%         clear N_local N_localBlock
%         clear S S_LB S_UB
%         clear ST ST_LB ST_UB
        % 
        save(['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)},'/GSA.mat'],...
            'StableS','StableST',...
            'StableS_bounds','StableST_bounds',...
            'Sformula','STformula');
        
        indices{j}=[StableS,StableST];
        bounds{j}=[StableS_bounds,StableST_bounds];
        
    end
    clear j
    clear Y_A Y_B Y_Ab

    if strcmp(experiment{1},'empirical_joint') || strcmp(experiment{1},'filtered_joint')
        return;
    end

    outputsModified=outputs;
    for j = 1 : numOutputs
        outputsModified(outputs(:,j)==100000|outputs(:,j)==999999,j)=NaN;
    end
    clear j
%     if numInputs==2
%         numRows=1;
%         numColumns=2;
%     elseif numInputs==3 || numInputs==4
%         numRows=2;
%         numColumns=2;
%     elseif numInputs==5 || numInputs==6
%         numRows=2;
%         numColumns=3;
%     elseif numInputs==7 || numInputs==8 || numInputs==9
%         numRows=3;
%         numColumns=3;
%     elseif numInputs==10 || numInputs==11 || numInputs==12
%         numRows=3;
%         numColumns=4;
%     elseif numInputs==13 || numInputs==14 || numInputs==15 || numInputs==16
%         numRows=4;
%         numColumns=4;
%     elseif numInputs>16
%         return;
%     end
    % numRows=5;
    % numColumns=6;
    % 
    % for j = 1 : numOutputs
    %     if isnan(outputThresholds(j))==0
    %        outputsModified(outputsModified(:,j)>outputThresholds(j),j)=NaN;
    %     end
    % end
    % clear j
    % stepSize=50;
%     for j = 1 : numOutputsAnalysis
%         if isnan(outputThresholdsFigure(indexOutputs(j)))==0
%             maxY=outputThresholdsFigure(indexOutputs(j));
%         else
%             maxY=Inf;
%         end
%         inputs(:,1)=round(inputs(:,1));
%         for i = 1 : numInputs
%             X=inputs(:,i);
%             Y=outputsModified(:,indexOutputs(j));
%             Xbin=X(find(isnan(Y)==0));
%             Ybin=Y(find(isnan(Y)==0));
%             par=i;
%             meanBin=[];
%             bins=[LB(par):((UB(par)-LB(par))/stepSize):UB(par)];
%             for k=1:length(bins)-1
%                 binY=Ybin(find(Xbin>=bins(k) & Xbin<=bins(k+1) ));
%                 binX=Xbin(find(Xbin>=bins(k) & Xbin<=bins(k+1) ));
%                 meanBin(k,:)=[mean(binX) mean(binY)];
%             end
%             clear k Xbin Ybin
% 
%             if i==1
%                 Subplot=figure('InvertHardcopy','off','Color',[1 1 1],'Units','normalized','Position',[0.05 0.05 0.9 0.85],'PaperUnits','centimeters','PaperPosition',[0 0 40 30]);
%             end 
%             subplot(numRows,numColumns,indexInputs(i));
%             plot(X,Y,'.','MarkerSize',1);
%             xlim([LB(i) UB(i)]);
%             ylim([min(Y) min(maxY,max(Y))]);
%             hold on
%             plot(meanBin(:,1), meanBin(:,2),'.r','MarkerSize',5)
%             hold off
% %             Title=sprintf('%s against %s',textOutputs{indexOutputs(j)},textInputs{indexInputs(i)});
% %             title(Title,'FontWeight','bold','FontSize',10);
%             xlabel(textInputs{indexInputs(i)},'FontWeight','bold','FontSize',10);
%             ylabel(textOutputs{indexOutputs(j)},'FontWeight','bold','FontSize',10);
%             clear Title
%         end
%         clear i
%         clear X Y
% %         DestinationFolder=['Results/',dataset,'/model_',num2str(model),'/',experiment{1},tag,'/',textOutputs{indexOutputs(j)}];
% %         saveas(Subplot,[DestinationFolder,'/FIG/Scatters.fig']);
% %         saveas(Subplot,[DestinationFolder,'/JPEG/Scatters.jpeg']);
% %         saveas(Subplot,[DestinationFolder,'/EMF/Scatters.emf']);
%         if strcmp(experiment{2},'trajFixed')
%             localTag=['_',vehicleTag];
%         else
%             localTag=tag;
%         end
%         saveas(Subplot,['Results/Figures/scatterPlots_',dataset,'_model_',num2str(model),'_',experiment{1},localTag,'_',textOutputs{indexOutputs(j)},'.jpeg']);
%         clear localTag
%         close(Subplot);
%         clear Subplot DestinationFolder
%         clear maxY
%     end
    clear j
    clear stepSize
    clear outputsModified
    clear IndexThreshold
    
end

function value=getObjFunction(x,labels,model,dataVehicle,numReplications,indexGOF,percentileGOF)
    [outputsReps,dataVehicleReps]=Simulation(x,labels,model,dataVehicle,numReplications);
    if max(isnan(outputsReps),[],'all')==1
        error('Simulation error (calibration x=%s).',mat2str(x));
    end
    outputs=getBestReplication(outputsReps,dataVehicleReps,indexGOF,percentileGOF);
    value=outputs(indexGOF);
end

function [outputs,dataVehicle]=getBestReplication(outputsReps,dataVehicleReps,indexGOF,percentileGOF)
    values=sort(outputsReps(:,indexGOF));
    if percentileGOF==0
        index=1;
    else
        index=find(outputsReps(:,indexGOF)==values(ceil(percentileGOF*length(values))),1);
    end
    outputs=outputsReps(index,:);
    dataVehicle=dataVehicleReps{index};
    clear values index
end

function sample=copula(T,marginals,cholMatrix)
    numInputs=size(marginals,2);
    p=normcdf(norminv(T,0,1)*cholMatrix,0,1);
    sample=zeros(1,numInputs);
    for j = 1 : numInputs
        sample(1,j)=marginals(ceil(size(marginals,1)*p(1,j)),j);
    end
    clear j
    clear p
end

function modelBounds=ParseBounds(labels)
    fid=fopen('Data/Models/bounds.txt');
    boundsData=textscan(fid,'%s');
    fclose(fid);
    clear fid ans
    for i = 1 : length(boundsData{1})
        eval(boundsData{1}{i});
    end
    clear i
    clear boundsData
    fields=fieldnames(bounds);
    for i = 1 : length(fields)
        bounds.(fields{i})=bounds.(fields{i})';
    end
    clear i
    clear fields
    modelBounds=[];
    for i = 1 : length(labels)
        modelBounds=[modelBounds,bounds.(labels{i})];
    end
    clear i
end

function dataVehicles=loadingData(dataset,resamplingRate)
    files=dir(['Data/Vehicles/',dataset]);
    files=files(3:end);
    dataVehicles=[];
    vehIDs=[];
    if strcmp(dataset,'NGSIM') || strcmp(dataset,'ZEN')
        dt=0.1;
    elseif strcmp(dataset,'HIGHD')
        dt=0.04;
    end
    for i = 1 : length(files)
        data=load(['Data/Vehicles/',dataset,'/',files(i).name]);
        vehIDs=[vehIDs;data.dataVehicle.id(1)];
        dataVehicles=[dataVehicles;{parseData(data.dataVehicle,dt,resamplingRate)}];
        clear data
    end
    clear i
    clear files
    [~,index]=sortrows(vehIDs);
    dataVehicles=dataVehicles(index);
    clear vehIDs
end

function dataVehicle=parseData(data,dt,resamplingRate)
    dataVehicle.dt=dt;
    dataVehicle.xFoll=data.x;
    dataVehicle.vFoll=data.v;
    dataVehicle.aFoll=data.a;
    dataVehicle.xLead=data.xLeader;
    dataVehicle.vLead=data.vLeader;
    dataVehicle.aLead=data.aLeader;
    dataVehicle.lengthLead=data.vehLengthLeader;
    if isnan(resamplingRate)==0
       dataVehicle=resampling(dataVehicle,resamplingRate);
    end
end

function resampledDataVehicle=resampling(dataVehicle,resamplingRate)
    resampledDataVehicle.dt=resamplingRate;
    resampledDataVehicle.vLead=[];
    resampledDataVehicle.vFoll=[];
    resampledDataVehicle.lengthLead=[];
    for i = 1 : size(dataVehicle.vLead,1)
        if i==1
            resampledDataVehicle.vLead=dataVehicle.vLead(i,:);
            resampledDataVehicle.vFoll=dataVehicle.vFoll(i);
            resampledDataVehicle.lengthLead=dataVehicle.lengthLead(i,:);
        else
            accLead=(dataVehicle.vLead(i,:)-dataVehicle.vLead(i-1,:))/dataVehicle.dt;
            accFoll=(dataVehicle.vFoll(i)-dataVehicle.vFoll(i-1))/dataVehicle.dt;
            for j = 1 : dataVehicle.dt/resamplingRate
                resampledDataVehicle.lengthLead=[resampledDataVehicle.lengthLead;dataVehicle.lengthLead(i,:)];
                resampledDataVehicle.vLead=[resampledDataVehicle.vLead;resampledDataVehicle.vLead(end,:)+accLead*resamplingRate];
                resampledDataVehicle.vFoll=[resampledDataVehicle.vFoll;resampledDataVehicle.vFoll(end)+accFoll*resamplingRate];
            end
            clear j
            clear accLead accFoll
        end
    end
    clear i
    resampledDataVehicle.aLead=[dataVehicle.aLead(1,:);diff(resampledDataVehicle.vLead)/resamplingRate];
    resampledDataVehicle.aFoll=[dataVehicle.aFoll(1);diff(resampledDataVehicle.vFoll)/resamplingRate];
    resampledDataVehicle.xLead=zeros(size(resampledDataVehicle.vLead));
    resampledDataVehicle.xFoll=zeros(length(resampledDataVehicle.vFoll),1);
    for i = 1 : size(resampledDataVehicle.vLead,1)
        if i==1
            resampledDataVehicle.xLead(i,:)=dataVehicle.xLead(i,:);
            resampledDataVehicle.xFoll(i)=dataVehicle.xFoll(i);
        else
            resampledDataVehicle.xLead(i,:)=resampledDataVehicle.xLead(i-1,:)+(resampledDataVehicle.vLead(i,:)+resampledDataVehicle.vLead(i-1,:))*resampledDataVehicle.dt/2;
            resampledDataVehicle.xFoll(i)=resampledDataVehicle.xFoll(i-1)+(resampledDataVehicle.vFoll(i)+resampledDataVehicle.vFoll(i-1))*resampledDataVehicle.dt/2;
        end
    end
    clear i
end