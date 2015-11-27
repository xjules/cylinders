function A01ReadXframeScanData

close all, clear all


try
    load('lig.mat');
catch
    src='ligand.txt';
    D=readLigandData(src); 
    save('lig.mat','D');
    fprintf('building matrix...\n');
end

try
    load('as.mat');
catch
    A=readActiveSiteData('active_site.txt');
    save('as.mat');
    fprintf('building as mat...');
end

figure
tmp = get(gca,'colororder');
P=wavelet3d(D.x,D.y,D.z,10);
i=1;
plot3(P{i}.x,P{i}.y,P{i}.z,'Color',tmp(i,:),'linewidth',1);
hold on;
j=2;
for i=2:3:10
    plot3(P{i}.x,P{i}.y,P{i}.z,'Color',tmp(j,:),'linewidth',1);
    j=j+1;
    hold on;
end




R0 = getGraph(D,A,1);
dR0 = diff(R0);
ddR0 = diff(dR0);
zcR0=find(ddR0(1:end-1).*ddR0(2:end)<0);
R1 = getGraph(D,A,10);
dR1 = diff(R1);
ddR1 = diff(dR1);
% zcR1=find(ddR1(1:end-1).*ddR1(2:end)<0);

R2 = getGraph(D,A,50);
dR2 = diff(R2);
ddR2 = diff(dR2);
% zcR2=find(ddR2(1:end-1).*ddR2(2:end)<0);

R3 = getGraph(D,A,100);
dR3 = diff(R3);
ddR3 = diff(dR3);
% zcR3=find(ddR3(1:end-1).*ddR3(2:end)<0);

R4 = getGraph(D,A,300);
dR4 = diff(R4);
ddR4 = diff(dR4);
% zcR4=find(ddR4(1:end-1).*ddR4(2:end)<0);
figure
Dw=waveletDistance(R0,10);
% struct2csv(Dw,'csvDst.txt');

i=1;
plot(1:(2^(i-1)):2162,Dw{i}.r,'Color',tmp(i,:),'linewidth',1);
hold on;
j=2;
for i=2:3:10
    plot(1:(2^(i-1)):2162,Dw{i}.r,'Color',tmp(j,:),'linewidth',1);
    j=j+1;
    hold on;
end
title('Distance to AS Wavelets');

save('DstWT.mat','Dw');
save('PosWT.mat','P');



% figure
%[Md,Xd,Ah,RId,Cat] = getNearAtoms(D);
% subplot(7,1,1);
% plot(D.keyframe,D.nearAtomsCount,'.-');
% title('#atoms nearby');
% subplot(7,1,2);
% plot(D.keyframe,Md,'.-',D.keyframe,Xd,'r.-');
% % fill([D.keyframe,Md],[D.keyframe,Xd],'b');
% title('Min/max distance to atoms');
% subplot(7,1,3);
% plot(D.keyframe,Ah,'.-');
% title('Average hydrophobicity');

figure


figure
subplot(5,1,1);
histogram(zcR0,10);
subplot(5,1,2);
histogram(zcR0,50);
subplot(5,1,3);
histogram(zcR0,100);
subplot(5,1,4);
histogram(zcR0,300);
subplot(5,1,5);
histogram(zcR0,600);

end


function [Md,Xd,Ah,RId,Categories]=getNearAtoms(inData)
numSteps=size(inData.x,2);
Md=[];
Xd=[];
Ah=[];
RId=[];
startIdx=1;
for j=1:numSteps
    atomCount = inData.nearAtomsCount(j);
    values = inData.nearAtomsInfo(startIdx:startIdx+6*atomCount-1);
    startIdx=startIdx+6*atomCount;
    T.atomID=[];
    T.distance=[];
    T.radius=[];
    T.residueID=[];
    T.residueName=[];
    T.hydrophobicity =[];
    %out = strsplit(values);
    out=values;
    for i=1:atomCount
        T.atomID = [T.atomID str2double(out((i-1)*6+1))];
        T.distance = [T.distance str2double(out((i-1)*6+2))];
        T.radius = [T.radius str2double(out((i-1)*6+3))];
        T.residueID = [T.residueID str2double(out((i-1)*6+4))];
        T.residueName = [T.residueName out((i-1)*6+5)];
        T.hydrophobicity = [T.hydrophobicity str2double(out((i-1)*6+6))];
    end
    Md = [Md min(T.distance)];
    Xd = [Xd max(T.distance)];
    Ah = [Ah mean(T.hydrophobicity)];
    RId = [RId int32(T.residueID)];
end
Categories=unique(RId);
end

function R=getGraph(D,A, step)

    R = sqrt((D.x(1:step:end) - A.x(1:step:end)).^2 + (D.y(1:step:end) - A.y(1:step:end)).^2 ...
    + (D.z(1:step:end) - A.z(1:step:end)).^2);
    
end

function A=readActiveSiteData(fname)
%keyframe,x,y,z,number of atoms nerby,[atomID, distance, radius, residueID, residueName, hydrophobicity]
A.keyframe=[];
A.x=[];
A.y=[];
A.z=[];
fid=fopen(fname);
i=0;
while(~feof(fid))
     str=fgetl(fid);
     if i>0 
         out = strsplit(str(1:end),',');
         A.keyframe = [A.keyframe out(1)];
         A.x = [A.x out(2)];
         A.y = [A.y out(3)];
         A.z = [A.z out(4)];
     end
     i=i+1;
end
fclose(fid);
A.keyframe = cellfun(@str2num,A.keyframe);
A.x = cellfun(@str2num,A.x);
A.y = cellfun(@str2num,A.y);
A.z = cellfun(@str2num,A.z);
end

function P=waveletDistance(r,N)
P={};
name='sym4';
P{1}.r=r;
for i=2:N
    [P{i}.r, xl]=dwt(P{i-1}.r,name);
    temp = idwt(P{i}.r,zeros(size(xl),'like',xl),name);
    P{i}.r = temp(1:2:end);
    matDst = [P{i}.r];
    csvwrite('Dst'+i+'.txt',matPos);
end
end

function P=wavelet3d(x,y,z,N)
P={};
name='sym4';
P{1}.x=x;
P{1}.y=y;
P{1}.z=z;
for i=2:N
    [P{i}.x, xl]=dwt(P{i-1}.x,name);
    temp = idwt(P{i}.x,zeros(size(xl),'like',xl),name);
    P{i}.x = temp(1:2:end);
    
    [P{i}.y, yl]=dwt(P{i-1}.y,name);
    temp = idwt(P{i}.y,zeros(size(yl),'like',yl),name);
    P{i}.y = temp(1:2:end);
    
    [P{i}.z, zl]=dwt(P{i-1}.z,name);
    temp = idwt(P{i}.z,zeros(size(zl),'like',zl),name);
    P{i}.z = temp(1:2:end);
    
    matPos = [P{i}.x P{i}.y P{i}.z];
    csvwrite('Pos'+i+'.txt',matPos);
end
end

function D=readLigandData(fname)
%keyframe,x,y,z,number of atoms nerby,[atomID, distance, radius, residueID, residueName, hydrophobicity]
D.keyframe = [];
D.x=[];
D.y=[];
D.z=[];
D.nearAtomsCount=[];
D.nearAtomsInfo={};
fid=fopen(fname);
i=0;
while(~feof(fid))
     str=fgetl(fid);
     if i>0 
         out = strsplit(str(1:end-1),',');
         D.keyframe = [D.keyframe out(1)];
         D.x = [D.x out(2)];
         D.y = [D.y out(3)];
         D.z = [D.z out(4)];
         D.nearAtomsCount = [D.nearAtomsCount out(5)];
         D.nearAtomsInfo = [D.nearAtomsInfo out(6:end)];
     end
     i=i+1;
end
fclose(fid);
D.keyframe = cellfun(@str2num,D.keyframe);
D.x = cellfun(@str2num,D.x);
D.y = cellfun(@str2num,D.y);
D.z = cellfun(@str2num,D.z);
D.nearAtomsCount = cellfun(@str2num,D.nearAtomsCount);
%     
%     function v=str2data(str)
%          s=strrep(rem,',',' ');
%          n=str2num(s);
%          t=datenum(n(3),n(2),n(1),n(4),n(5),n(6));
%          v=[t n(7:end)];
%     end
    
end

