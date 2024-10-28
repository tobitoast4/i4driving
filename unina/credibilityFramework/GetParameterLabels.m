function [...
        labels,...
        baseID,...
        cognitiveID,...
        spatialID,...
        perceptionErrorID,...
        perceptionDelayID,...
        temporalID...
    ]=GetParameterLabels(model)

    baseIndex=model;
    baseID=ceil(baseIndex/160);
    cognitiveIndex=baseIndex-(baseID-1)*160;
    cognitiveID=ceil(cognitiveIndex/40);
    spatialIndex=cognitiveIndex-(cognitiveID-1)*40;
    spatialID=ceil(spatialIndex/20);
    perceptionErrorIndex=spatialIndex-(spatialID-1)*20;
    perceptionErrorID=ceil(perceptionErrorIndex/5);
    perceptionDelayIndex=perceptionErrorIndex-(perceptionErrorID-1)*5;
    if perceptionDelayIndex==1
        perceptionDelayID=1;
        temporalID=1;
    else
        perceptionDelayID=ceil((perceptionDelayIndex-1)/2)+1;
        temporalID=(perceptionDelayIndex-1)-(perceptionDelayID-2)*2;
    end
    clear baseIndex cognitiveIndex spatialIndex perceptionErrorIndex perceptionDelayIndex

    labels={'a','b','s0','T','deltaV0','delta'};
    if baseID==4 || baseID==5
        labels=[labels,'vCritPerc'];
    end
    
    if cognitiveID==2
        labels=[labels,'gammaTD','betaTD'];
    elseif cognitiveID==3
        labels=[labels,'hExp','TC','TScrit','deltaTSmax','deltaSAmax','betaT'];
    elseif cognitiveID==4
        labels=[labels,'vCritPerc','deltaTmax','Tprob','TminHigh','deltaTmaxHigh','TprobHigh','deltaTcomf'];
    end
    
    if spatialID==2
        labels=[labels,'muX_1','muX_2'];
    end
    
    if perceptionErrorID>=2
        labels=[labels,'eps_s','eps_v','eps_dv'];
        if perceptionErrorID==3
            labels=[labels,'epsSA','hExp','TC','TScrit','deltaTSmax','deltaSAmax'];
        elseif perceptionErrorID==4
            labels=[labels,'epsTau','epsW_s','epsW_v','epsW_dv'];
        end
    end
    
    if perceptionDelayID>=2
        labels=[labels,'tp'];
        if perceptionDelayID==3
            labels=[labels,'tpSA','hExp','TC','TScrit','deltaTSmax','deltaSAmax'];
        end
    end
    
    if temporalID==2
        labels=[labels,'muT_s','muT_v'];
    end
    
    labels=unique(labels,'stable');
    
end