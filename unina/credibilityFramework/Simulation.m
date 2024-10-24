function [outputsReps,dataVehicleReps]=Simulation(parameters,labels,model,dataVehicle,numReplications)

    % Init
    parameters=init(parameters,labels,model,dataVehicle);
    if parameters.stochastic==1
        rng('default');
    else
        numReplications=1;
    end
    
    % Run replications
    outputsReps=zeros(numReplications,7);
    dataVehicleReps=cell(numReplications,1);
    % tic;
    for i = 1 : numReplications
        [outputsReps(i,:),dataVehicleReps{i}]=run(parameters,dataVehicle);
    end
    clear i
    % fprintf(' (%.5f seconds)\n',toc);
    
end

function [modelParameters]=init(parameters,labels,model,dataVehicle)
    modelParameters.base=ceil(model/160);    
    modelParameters.epsW_constant=0;
    if max(strcmp(labels,'epsW_s'))==1 ||...
            max(strcmp(labels,'deltaTmax'))==1
        modelParameters.stochastic=1;
        if max(strcmp(labels,'epsW_s'))==1
            modelParameters.epsW_constant=1;
        end
    else
        modelParameters.stochastic=0;
    end
    modelParameters.a=NaN;
    modelParameters.b=NaN;
    modelParameters.s0=NaN;
    modelParameters.T=NaN;
    modelParameters.deltaV0=NaN;
    modelParameters.delta=NaN;
    modelParameters.vCritPerc=0;
    modelParameters.tp=0;
    modelParameters.ta=0;
    modelParameters.tpSA=0;
    modelParameters.muT_s=0;
    modelParameters.muT_v=0;
    modelParameters.muX_1=1;
    modelParameters.muX_2=0.5;
    modelParameters.eps_s=1;
    modelParameters.eps_v=1;
    modelParameters.eps_dv=1;
    modelParameters.epsSA=0;
    modelParameters.epsTau=Inf;
    modelParameters.epsW_s=0;
    modelParameters.epsW_v=0;
    modelParameters.epsW_dv=0;
    modelParameters.hExp=0;
    modelParameters.TC=0;
    modelParameters.TScrit=0;
    modelParameters.deltaTSmax=0;
    modelParameters.deltaSAmax=0;
    modelParameters.betaT=0;
    modelParameters.gammaTD=0;
    modelParameters.betaTD=0;
    modelParameters.deltaTmax=0;
    modelParameters.Tprob=0;
    modelParameters.TminHigh=0;
    modelParameters.deltaTmaxHigh=0;
    modelParameters.TprobHigh=0;
    modelParameters.deltaTcomf=0;
    for i = 1 : length(labels)
        modelParameters.(labels{i})=parameters(i);
    end
    clear i
    modelParameters.v0=max(dataVehicle.vFoll)+modelParameters.deltaV0;
    modelParameters.vCrit=modelParameters.v0*modelParameters.vCritPerc;
    modelParameters.Tmax=modelParameters.T+modelParameters.deltaTmax;
    modelParameters.TmaxHigh=modelParameters.TminHigh+modelParameters.deltaTmaxHigh;
    modelParameters.tp=round(modelParameters.tp/dataVehicle.dt);
%     modelParameters.tpSA=round(modelParameters.tpSA/dataVehicle.dt);
    modelParameters.ta=round(modelParameters.ta/dataVehicle.dt);
    modelParameters.TSmax=modelParameters.TScrit+modelParameters.deltaTSmax;
    modelParameters.SAmin=0;
    modelParameters.SAmax=modelParameters.SAmin+modelParameters.deltaSAmax;
    modelParameters.epsTau=round(modelParameters.epsTau/dataVehicle.dt);
    modelParameters.muX_2=min(modelParameters.muX_1,(1-modelParameters.muX_1)*modelParameters.muX_2);
    modelParameters.muX_3=1-modelParameters.muX_1-modelParameters.muX_2;
    modelParameters.muX=[...
        modelParameters.muX_1,...
        modelParameters.muX_2,...
        modelParameters.muX_3...
    ];
end

function [outputs,dataVehicle]=run(parameters,dataVehicle)
    
    % Init parameters
    base=parameters.base;
    a=parameters.a;
    b=parameters.b;
    s0=parameters.s0;
    T=parameters.T;
    v0=parameters.v0;
    delta=parameters.delta;
    vCrit=parameters.vCrit;
    tp=parameters.tp;
    ta=parameters.ta;
    tpSA=parameters.tpSA;
    muT_s=parameters.muT_s;
    muT_v=parameters.muT_v;
    muX=parameters.muX;
    eps_s=parameters.eps_s;
    eps_v=parameters.eps_v;
    eps_dv=parameters.eps_dv;
    epsSA=parameters.epsSA;
    epsTau=parameters.epsTau;
    epsW_constant=parameters.epsW_constant;
    epsW_s=parameters.epsW_s;
    epsW_v=parameters.epsW_v;
    epsW_dv=parameters.epsW_dv;
    hExp=parameters.hExp;
    TC=parameters.TC;
    TScrit=parameters.TScrit;
    TSmax=parameters.TSmax;
    SAmin=parameters.SAmin;
    SAmax=parameters.SAmax;
    betaT=parameters.betaT;
    gammaTD=parameters.gammaTD;
    betaTD=parameters.betaTD;
    Tmax=parameters.Tmax;
    Tprob=parameters.Tprob;
    TminHigh=parameters.TminHigh;
    TmaxHigh=parameters.TmaxHigh;
    TprobHigh=parameters.TprobHigh;
    deltaTcomf=parameters.deltaTcomf;
    
    % Init data
    xLead=dataVehicle.xLead;
    vLead=dataVehicle.vLead;
    lengthLead=dataVehicle.lengthLead;
    xFoll=dataVehicle.xFoll;
    vFoll=dataVehicle.vFoll;
    aFoll=dataVehicle.aFoll;
    xFollSim=ones(length(xFoll),1)*NaN;
    vFollSim=ones(length(vFoll),1)*NaN;
    aFollSim=ones(length(aFoll),1)*NaN;
    xFollSim(1:1+round(SAmax*tpSA)+tp)=xFoll(1:1+round(SAmax*tpSA)+tp);
    vFollSim(1:1+round(SAmax*tpSA)+tp)=vFoll(1:1+round(SAmax*tpSA)+tp);
    aFollSim(1:1+round(SAmax*tpSA)+tp)=aFoll(1:1+round(SAmax*tpSA)+tp);
    
    % Init variables
    Tvar=ones(length(xFoll),1)*NaN;
    TS=ones(length(xFoll),1)*NaN;
    SA=ones(length(xFoll),1)*NaN;
    w_s=ones(length(xFoll),1)*NaN;
    w_v=ones(length(xFoll),1)*NaN;
    w_dv=ones(length(xFoll),1)*NaN;
    randomU=rand(length(xFoll),1);
    randomN=randn(length(xFoll),3);
    
    % Simulation
    crash=0;
    dt=dataVehicle.dt;
    for i = 1 : length(xFoll)-1
        if crash==0 && ...
                xLead(i)-lengthLead(i)-xFollSim(i)<=0
            crash=1;
        end
        if crash==1
            vFollSim(i+1)=0;
            xFollSim(i+1)=xFollSim(i);
            continue;
        end
        
        if TC==0
            TS(i)=0;
        else
            TS(i)=exp(-((xLead(i)-lengthLead(i)-xFollSim(i))/vFollSim(i))/hExp)/TC;
        end
        
        if TS(i)<=TScrit
            SA(i)=SAmax;
        elseif TS(i)<TSmax
            SA(i)=SAmax-((TS(i)-TScrit)/(TSmax-TScrit))*(SAmax-SAmin);
        else
            SA(i)=SAmin;
        end
        
        if i==1
            Tvar(i)=T;
            w_s(i)=0;
            w_dv(i)=0;
            w_v(i)=0;
        else
            if vFollSim(i)<=vCrit
                if randomU(i)<Tprob
                    Tvar(i)=T+randomU(i)*(Tmax-T);
                else
                    Tvar(i)=Tvar(i-1);
                end
            else
                if randomU(i)<TprobHigh
                    Tvar(i)=TminHigh+randomU(i)*(TmaxHigh-TminHigh);
                else
                    Tvar(i)=Tvar(i-1);
                end
            end
            if Tvar(i)<Tvar(i-1)
                Tvar(i)=max(Tvar(i)-deltaTcomf,Tvar(i));
            else
                Tvar(i)=min(Tvar(i)+deltaTcomf,Tvar(i));
            end
            Tvar(i)=max(Tvar(i),0.1);
            w_s(i)=exp(-1/epsTau)*w_s(i-1)+sqrt(2/epsTau)*randomN(i,1);
            w_dv(i)=exp(-1/epsTau)*w_dv(i-1)+sqrt(2/epsTau)*randomN(i,2);
            w_v(i)=exp(-1/epsTau)*w_v(i-1)+sqrt(2/epsTau)*randomN(i,3);
        end
        
        tpVar=round((SAmax-SA(i))*tpSA)+tp;
        
        if (i-(round(SAmax*tpSA)+tp))<1
            continue;
        end
        
        v=max(0,...
            (eps_v+...
            epsW_constant*exp(epsW_v*w_v(i-tpVar))+...
            epsSA*(SAmax-SA(i-tpVar)))*...
            vFollSim(i-tpVar)...
        );
    
        sAvg=0;
        dvAvg=0;
        for j = 1 : length(muX)
            sAvg=sAvg+muX(j)*(xLead(i-tpVar,j)-lengthLead(i-tpVar,j)-xFollSim(i-tpVar));
            dvAvg=dvAvg+muX(j)*(vLead(i-tpVar,j)-vFollSim(i-tpVar));
        end
        
        s=max(1e-5,...
            (eps_s+...
            epsW_constant*exp(epsW_s*w_s(i-tpVar))+...
            epsSA*(SAmax-SA(i-tpVar)))*...
            sAvg...
        );
    
        dv=(eps_dv+...
            epsSA*(SAmax-SA(i-tpVar)))*...
            dvAvg+...
            -epsW_constant*epsW_dv*w_dv(i-tpVar)*sAvg;
        
        s=max(1e-5,s+muT_s*dv*tpVar*dt);
        v=max(0,v+muT_v*aFollSim(i-tpVar)*tpVar*dt);
        
        Tvar(i)=Tvar(i)*(1+max(0,betaT*(TS(i-tpVar)-TScrit)));
        TD=((Tvar(i)*v)/((1-betaTD)*s))^gammaTD;
        
        sDesired=s0+max(0,Tvar(i)*v-v*dv/(2*sqrt(a*b)));
        
        if base==1 % idm
            aModel=a*(1-(v/v0)^delta-(sDesired*TD/s)^2);
        elseif base==2 % idm+
            aModel=a*min(...
                (1-(v/v0)^delta),...
                (1-(sDesired*TD/s)^2));
        elseif base==3 % iidm
            if sDesired*TD<=s
                aModel=a*(1-(v/v0)^delta)*(1-(sDesired*TD/s)^(2/(1-(v/v0)^delta)));
            else
                aModel=a*(1-(sDesired*TD/s)^2);
            end
        elseif base==4 || base==5 % iidm+, midm
            if sDesired*TD<=s
                if base==4
                    aModel=a*(1-(v/v0)^delta)*(1-(sDesired*TD/s)^2);
                else
                    aModel=a*(1-(v/v0)^delta-(sDesired*TD/s)^2);
                end
            else
                if v<=vCrit
                    aModel=a*(1-(sDesired*TD/s)^2);
                else
                    aModel=min(-b,a*(1-(sDesired*TD/s)^2));
                end
            end
        end
        
%         aModel=(ta*aFollSim(i)+aModel)/(1+ta);

        vFollSim(i+1)=max(0,vFollSim(i)+aModel*dt);
        aFollSim(i+1)=(vFollSim(i+1)-vFollSim(i))/dt;
        xFollSim(i+1)=xFollSim(i)+(vFollSim(i)+vFollSim(i+1))*dt/2;
    end
    
    dataVehicle.xFollSim=xFollSim;
    dataVehicle.vFollSim=vFollSim;
    dataVehicle.aFollSim=aFollSim;
    
    netSpacingObs=dataVehicle.xLead(:,1)-dataVehicle.lengthLead(:,1)-dataVehicle.xFoll;
    netSpacingSim=dataVehicle.xLead(:,1)-dataVehicle.lengthLead(:,1)-dataVehicle.xFollSim;

    % Error definition
    errorsSpacing=netSpacingSim(1+round(SAmax*tpSA)+tp+1:end)-netSpacingObs(1+round(SAmax*tpSA)+tp+1:end);
    errorsSpeed=dataVehicle.vFollSim(1+round(SAmax*tpSA)+tp+1:end)-dataVehicle.vFoll(1+round(SAmax*tpSA)+tp+1:end);
    
    % GOF definition
    rmse_s=sqrt(mean((errorsSpacing).^2));
    rmse_v=sqrt(mean((errorsSpeed).^2));
    nrmse_s=rmse_s/sqrt(mean(netSpacingObs(1+round(SAmax*tpSA)+tp+1:end).^2));
    nrmse_v=rmse_v/sqrt(mean(dataVehicle.vFoll(1+round(SAmax*tpSA)+tp+1:end).^2));
    nrmse_s_v=nrmse_s+nrmse_v;
    minNetSpacing=min(netSpacingSim);
    vFollMinNetSpacing=dataVehicle.vFollSim(find(netSpacingSim==minNetSpacing,1));
    vLeadMinNetSpacing=dataVehicle.vLead(find(netSpacingSim==minNetSpacing,1),1);
    xFollTot=dataVehicle.xFollSim(end);
    
    outputs=[...
        rmse_s,...
        rmse_v,...
        nrmse_s_v,...
        minNetSpacing,...
        vFollMinNetSpacing,...
        vLeadMinNetSpacing,...
        xFollTot];

end