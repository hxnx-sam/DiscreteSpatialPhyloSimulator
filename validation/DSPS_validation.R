# R functions and scripts to validate DSPS
# S. J. Lycett
# 31 Dec 2014
# 11 Jan 2015
# 23 Mar 2015
# 25 Oct 2015

# see http://cran.r-project.org/web/packages/deSolve/vignettes/deSolve.pdf

library(deSolve)
library(ape)


###########################################################



############################################
# define SIR for ODEs (1 deme)

makeSIRparameters	<- function(b=0.1, y=0.05, N=1000) {
	parameters <- c(b=b/N, y=y)
	return (parameters)
}

makeSIRInitialState	<- function(N=1000, I=1) {
	state	   <- c(S=(N-I), I=I, R=0)
	return (state)
}


# define SIR function for ODEs
SIR <- function(t, state, parameters) {
	with( as.list(c(state, parameters)),
		{
 			# rate of change
 			dS <- -b*S*I
	 		dI <- b*S*I - y*I
 			dR <- y*I
	
			# return the rate of change
 			list( c(dS, dI, dR) )
 		}
	)
}

############################################
# define SEIR for ODEs (1 deme)

makeSEIRparameters	<- function(a=1, b=0.1, y=0.05, N=1000) {
	parameters <- c(a=a, b=b/N, y=y)
	return (parameters)
}

makeSEIRInitialState	<- function(N=1000, E=0, I=1) {
	state	   <- c(S=(N-I-E), E=E, I=I, R=0)
	return (state)
}

# define SEIR function for ODEs
SEIR <- function(t, state, parameters) {
	with( as.list(c(state, parameters)),
		{
 			# rate of change
 			dS <- -b*S*I
 			dE <- b*S*I - a*E
	 		dI <- a*E - y*I
 			dR <- y*I
	
			# return the rate of change
 			list( c(dS, dE, dI, dR) )
 		}
	)
}

############################################
# define SIR for two demes


makeSIR2parameters	<- function(b11=0.1, y1=0.05, N1=1000,
								b22=0.1, y2=0.05, N2=1000,
								b12=b11/2, b21=b22/2) {
	parameters <- c(b11=b11/N1, b12=b12/N1, b21=b21/N2, b22=b22/N2, y1=y1, y2=y2)
	return (parameters)
}

makeSIR2InitialState	<- function(N1=1000, I1=1, N2=1000, I2=0) {
	state	   <- c(S1=(N1-I1), I1=I1, R1=0, S2=(N2-I2), I2=I2, R2=0)
	return (state)
}

# define SEIR function for ODEs
SIR2 <- function(t, state, parameters) {
	with( as.list(c(state, parameters)),
		{
 			# rate of change
 			dS1 <- -b11*S1*I1 - b12*S1*I2
	 		dI1 <- (b11*S1*I1 + b12*S1*I2) - y1*I1
 			dR1 <- y1*I1
 			
			dS2 <- -b22*S2*I2 - b21*S2*I1
	 		dI2 <- (b22*S2*I2 + b21*S2*I1) - y2*I2
 			dR2 <- y2*I2
 			
	
			# return the rate of change
 			list( c(dS1, dI1, dR1, dS2, dI2, dR2) )
 		}
	)
}



############################################################
# ODE EXAMPLES
############################################################

doSIExample <- FALSE

if (doSIExample) {

	# define parameters (order is important)
	N	     	<- 1000
	parameters 	<- makeSIRparameters(b=0.2, y=0, N=N)
	state      	<- makeSIRInitialState(I=1, N=N)
	times      	<- seq(0, 100, by = 0.5)


	# run a model
	res <- ode(y = state, times = times, func = SIR, parms = parameters)

	# plot the results
	mainTxt <- paste("SI N=",N," beta=",parameters[1]*N,"/N",sep="")
	plot(res[,1], res[,2], type="l", ylim=c(0,1000), xlab="Time", ylab="Number", main=mainTxt, col="orange")
	lines(res[,1], res[,3], col="red")
	legend("left",c("S","I"),col=c("orange","red"),lty=1,bty="n")	
	
}

doSIRExample <- FALSE

if (doSIRExample) {

	# define parameters (order is important)
	N	     	<- 1000
	parameters 	<- makeSIRparameters(b=0.2, y=0.05, N=N)
	state      	<- makeSIRInitialState(I=1, N=N)
	times      	<- seq(0, 400, by = 0.5)


	# run a model
	res <- ode(y = state, times = times, func = SIR, parms = parameters)

	# plot the results
	mainTxt <- paste("SIR N=",N," beta=",parameters[1]*N,"/N gamma=",parameters[2],sep="")
	plot(res[,1], res[,2], type="l", ylim=c(0,1000), xlab="Time", ylab="Number", main=mainTxt, col="orange")
	lines(res[,1], res[,3], col="red")
	lines(res[,1], res[,4], col="blue")
	legend("left",c("S","I","R"),col=c("orange","red","blue"),lty=1,bty="n")	
	
}



doSEIRExample <- FALSE

if (doSEIRExample) {

	# define parameters (order is important)
	N	     	<- 1000
	parameters 	<- makeSEIRparameters(a=0.1, b=0.2, y=0.05, N=N)
	state      	<- makeSEIRInitialState(I=1, E=0, N=N)
	times      	<- seq(0, 400, by = 0.5)


	# run a model
	res <- ode(y = state, times = times, func = SEIR, parms = parameters)

	# plot the results
	mainTxt <- paste("SEIR N=",N," alpha=",parameters[1]," beta=",parameters[2]*N,"/N gamma=",parameters[3],sep="")
	plot(res[,1], res[,2], type="l", ylim=c(0,1000), xlab="Time", ylab="Number", main=mainTxt, col="orange")
	lines(res[,1], res[,3], col="green")
	lines(res[,1], res[,4], col="red")
	lines(res[,1], res[,5], col="blue")
	legend("left",c("S","E","I","R"),col=c("orange","green","red","blue"),lty=1,bty="n")	
	
}

doSIR2Example <- FALSE

if (doSIR2Example) {

	# define parameters (order is important)
	N1	     	<- 1000
	N2			<- 1000
	parameters 	<- makeSIR2parameters(b11=0.2, y1=0.05, N1=N1, b22=0.1, y2=0.05, N2=N2, b12=0.01, b21=0.01)
	state      	<- makeSIR2InitialState(I1=1, N1=N1, I2=0, N2=N2)
	times      	<- seq(0, 400, by = 0.5)


	# run a model
	res <- ode(y = state, times = times, func = SIR2, parms = parameters)

	cn  <- colnames(res)
	# plot the results
	mainTxt <- paste("SIR 2deme")
	plot(res[,1], res[,which(cn=="I1")], type="l", ylim=c(0,1000), xlab="Time", ylab="Number", main=mainTxt, col="red")
	lines(res[,1], res[,which(cn=="I2")], col="blue")
	legend("left",c("I1","I2"),col=c("red","blue"),lty=1,bty="n")	
	
}

###############################################################################################
# HELPER FUNCTIONS

# useful function to split a sequence name
# S. J. Lycett
# 19 May 2011
# 6  Oct 2011

getEl	<- function( line, sep=",", ind=-1, final=FALSE, reconstruct=FALSE, ex=-1, fromEnd=FALSE ) {
	els	<- strsplit(line, sep)[[1]]

	if (ind[1] != -1) {
		if (fromEnd) {
			ind <- length(els)-(ind-1)
		}
	}

	if (final) {
		return( els[length(els)] )
	} else {

		if (reconstruct) {
			if (ex[1] > 0) {
				if (fromEnd) {
					ex <- length(els)-(ex-1)
				}
				ind <- setdiff((1:length(els)),ex)
			}

			newLine <- els[ind[1]]
			if (length(ind) > 1) {
				for (i in 2:length(ind)) {
					newLine <- paste(newLine, els[ind[i]], sep=sep)
				}
			}
			return ( newLine )
		} else {
			if ( ind[1] == -1 ) {
				return( els )
			} else {
				return( els[ind] )
			}
		}
	}
}


# 2 May 2014 - Ran DSPS with 6 demes and line or star
addState_to_DSPS 		<- function( tr, tip.only=FALSE ) {
	taxa 			<- tr$tip.label
	deme 			<- apply(as.matrix(taxa), 1, getEl, ind=2, sep="_")
	tr$tip.state 	<- deme

	if (!tip.only) {
		nodeNames		<- tr$node.label
		node_deme		<- apply(as.matrix(nodeNames), 1, getEl, ind=2, sep="_")
		tr$node.state	<- node_deme

		tr$state		<- c(tr$tip.state, tr$node.state)
	}

	return( tr )
}


#####################################
# from population_fitness_features.R

distFromRoot	<- function( tr ) {

	rootNode	<- length(tr$tip.label)+1
	nodeDists	<- array(0, max(tr$edge))
	
	toProcess	<- c(rootNode)

	while ( length(toProcess) > 0 ) {
		einds 			<- which(tr$edge[,1]==toProcess[1])

		if (length(einds) > 0) {
			children 			<- tr$edge[einds,2]
			nodeDists[children] 	<- nodeDists[toProcess[1]] + tr$edge.length[einds]

			toProcess			<- c(toProcess, children)
		}
		toProcess			<- setdiff(toProcess, toProcess[1])
	}

	tr$nodeDists <- nodeDists

	return ( tr )
}

#####################################
# from population_fitness_features.R

nodeTimes	<- function(tr, youngestTip=2011.027) {

	if ( !any(attributes(tr)$names == "nodeDists") ) {
		tr <- distFromRoot(tr)
	}

	tr$rootHeight<- max(tr$nodeDists)
	tr$nodeTimes <- youngestTip - tr$rootHeight + tr$nodeDists

	return ( tr )
}


# 17 July 2012 - updated colours (previous didnt work if node states had fewer states than tip states)
plotPropertyTree	<- function( tr, pch=21, anc_pch=pch, cex=0.75, anc_cex=0.75, ustates=sort(unique(tr$tip.state)),
					 cols=rainbow( length(ustates) ), fill_anc=TRUE,
					 plot.tree=TRUE, show.tip.label=FALSE, show.node.label=FALSE, show.nodes=TRUE,
					 no.margin=FALSE, root.edge=TRUE, label.offset=0.1,
					 legendPosition="bottomright" ) {

	if (plot.tree) {
		plot(tr, 
		show.tip.label=show.tip.label, show.node.label=show.node.label, 
		no.margin=no.margin, root.edge=root.edge, label.offset=label.offset)
	}

	#ustates <- unique(tr$tip.state)
	ts	  <- match(tr$tip.state, ustates)
	ns 	  <- match(tr$node.state, ustates)

	tiplabels(pch=pch, bg=cols[ts], col="black", cex=cex)

	if (show.nodes) {
		if (fill_anc) {
			nodelabels(pch=anc_pch, bg=cols[ns], col="black", cex=anc_cex)
		} else {
			nodelabels(pch=anc_pch, col=cols[ns], cex=anc_cex)
		}
	}

	legend(legendPosition, ustates, pt.bg=cols, cex=cex, bty="n", pch=pch)
}

###############################################################################
# functions for DPSP outputs


loadTree <- function( fname="x", path=path, rootname=rootname, rep=1, ext="_binaryPruned.nwk") {
	
	if (fname=="x") {
		fname <- paste(path,rootname,"_",rep,ext,sep="")
	}
	
	tr 				<- read.tree(fname)
	tr				<- addState_to_DSPS(tr)
	tr				<- distFromRoot(tr)
	tr				<- nodeTimes(tr, youngestTip=max(tr$nodeDists))
	return (tr)
}



loadPopLog	<- function( fname="x", path=path, rootname=rootname, rep=1, ext="_popLog.csv" ) {
	
	if (fname=="x") {
		fname <- paste(path,rootname,"_",rep,ext,sep="")
	}
	
	data <- read.table(fname, skip=1, header=TRUE, sep=",")
	
	# remove ActiveHosts and Events columns
	cn	 <- colnames(data)
	exs	 <- c(which( cn=="ActiveHosts" ), which(cn=="Events"))
	incs <- setdiff(1:length(cn), exs)
	data <- data[,incs]
	
	return( data )
}

loadInfectionEventLog <- function( fname="x", path=path, rootname=rootname, rep=1, ext="_infectionEventLog.csv") {
	
	if (fname=="x") {
		fname <- paste(path,rootname,"_",rep,ext,sep="")
	}
	
	data 	<- read.table(fname, skip=2, header=TRUE, sep=",")
	fromTo	<- data[,c(4,5)]
	return( fromTo )
	
}

#########################################################################################################
# MAIN SCRIPT

doCompareSIR <- FALSE
if (doCompareSIR) {
	
	# set up the comparision ODEs
	N	     	<- 1000
	b			<- 0.1
	y			<- 0.05
	parameters 	<- makeSIRparameters(b=b, y=y, N=N)
	state      	<- makeSIRInitialState(I=1, N=N)
	times      	<- seq(0, 400, by = 0.5)

	# run ODE model
	ode_data 	<- ode(y = state, times = times, func = SIR, parms = parameters)
	
	# change this to your path
	#path 	 <- "Documents//workspace//DiscreteSpatialPhyloSimulator//validation//"
	path		 <- "D://data//phylo_inference//DSPS_testing//"
	
	# change this to your name
	rootname <- "ONE_DEME_SIR"
	
	# load the DSPS results
	nreps	 <- 100
	for (rep in 1:nreps) {
	
		dsps_data <- loadPopLog( path=path, rootname=rootname, rep=rep)
	
		# plot DSPS
		if (rep==1) {
			
			imageName <- paste(path,rootname,"_plots.png",sep="")
			png(file=imageName, height=1800, width=1800, res=300)
			
			plot( dsps_data[,1], dsps_data[,2], type="l", col="orange", 
				ylim=c(0,N), xlim=c(0,400),
				xlab="Time", ylab="Number" )
		} else {
			lines(dsps_data[,1], dsps_data[,2], col="orange")
		}
		lines(dsps_data[,1], dsps_data[,3], col="red")
		lines(dsps_data[,1], dsps_data[,4], col="blue")
	
	}
	
	# plot ODE
	lines( ode_data[,1], ode_data[,2], col="black", lty=2, lwd=2)
	lines( ode_data[,1], ode_data[,3], col="black", lty=3, lwd=2)
	lines( ode_data[,1], ode_data[,4], col="black", lty=4, lwd=2)
	title("DSPS and ODE SIR")
	legend("left", c("DSPS S","DSPS I", "DSPS R", "ODE S","ODE I", "ODE R"), lty=c(1,1,1,2,3,4), bty="n",
				col=c("orange","red","blue","black","black","black") )
	legend("right", c( paste("b=",b), paste("y=",y), paste("N=",N) ), bty="n"   )
	
	dev.off()
	
}
	
doCompareSEIR <- FALSE
if (doCompareSEIR) {
	
	# set up the comparision ODEs
	N	     	<- 1000
	a			<- 0.5
	b			<- 0.1
	y			<- 0.05
	parameters 	<- makeSEIRparameters(a=a, b=b, y=y, N=N)
	state      	<- makeSEIRInitialState(I=1, N=N)
	times      	<- seq(0, 400, by = 0.5)

	# run ODE model
	ode_data 	<- ode(y = state, times = times, func = SEIR, parms = parameters)
	
	# change this to your path
	#path 	 <- "Documents//workspace//DiscreteSpatialPhyloSimulator//validation//"
	path		 <- "D://data//phylo_inference//DSPS_testing//"
	
	# change this to your name
	rootname <- "ONE_DEME_SEIR"
	
	# load the DSPS results
	nreps	 <- 100
	for (rep in 1:nreps) {
	
		dsps_data <- loadPopLog( path=path, rootname=rootname, rep=rep)
	
		# plot DSPS
		if (rep==1) {
			imageName <- paste(path,rootname,"_plots.png",sep="")
			png(file=imageName, height=1800, width=1800, res=300)
			
			
			plot( dsps_data[,1], dsps_data[,2], type="l", col="orange", 
				ylim=c(0,N), xlim=c(0,400),
				xlab="Time", ylab="Number" )
		} else {
			lines(dsps_data[,1], dsps_data[,2], col="orange")
		}
		lines(dsps_data[,1], dsps_data[,3], col="green")
		lines(dsps_data[,1], dsps_data[,4], col="red")
		lines(dsps_data[,1], dsps_data[,5], col="blue")
	
	}
	
	# plot ODE
	lines( ode_data[,1], ode_data[,2], col="black", lty=2, lwd=2)
	lines( ode_data[,1], ode_data[,3], col="black", lty=5, lwd=2)
	lines( ode_data[,1], ode_data[,4], col="black", lty=3, lwd=2)
	lines( ode_data[,1], ode_data[,5], col="black", lty=4, lwd=2)
	title("DSPS and ODE SEIR")
	legend("left", c("DSPS S","DSPS E","DSPS I", "DSPS R", "ODE S","ODE E","ODE I", "ODE R"), lty=c(1,1,1,1,2,5,3,4), bty="n",
				col=c("orange","green","red","blue","black","black","black","black") )
	legend("right", c( paste("a=",a), paste("b=",b), paste("y=",y), paste("N=",N) ), bty="n"   )
	
	dev.off()
	
}

doCompareTwoSIR <- FALSE
if (doCompareTwoSIR) {
	
	# set up the comparision ODEs
	N1	     	<- 1000
	N2			<- 1000
	
	b			<- 0.1
	y			<- 0.05
	
	b11			<- b
	y1			<- y
	b12			<- b11*0.1
	
	b22			<- b
	y2			<- y
	b21			<- b22*0.1
	
	
	parameters 	<- makeSIR2parameters(b11=b11, y1=y1, N1=N1, b22=b22, y2=y2, N2=N2, b12=b12, b21=b21)
	state      	<- makeSIR2InitialState(I1=1, N1=N1, I2=0, N2=N2)
	times      	<- seq(0, 400, by = 0.5)

	# run a model
	ode_data <- ode(y = state, times = times, func = SIR2, parms = parameters)
	
	# change this to your path
	#path 	 <- "Documents//workspace//DiscreteSpatialPhyloSimulator//validation//"
	path		 <- "D://data//phylo_inference//DSPS_testing//"
	
	# change this to your name
	rootname <- "TWO_DEME_SIR"
	
	
	#imageName <- paste(path,rootname,"_plots.png",sep="")
	#png(file=imageName, height=1800, width=3600, res=300)
	#op <- par(mfrow=c(1,2))

	imageName <- paste(path,rootname,"_plots2.png",sep="")
	png(file=imageName, height=3000, width=1500, res=300)	
	op <- par(mfrow=c(2,1))
	
	# load the DSPS results (deme 0 and 1)
	N=N1
	
  for (d in c(0,3)) {
  	if (d==0) {
  		dd = 0
  	} else {
  		dd = 1
  	}
	nreps	 <- 100
	for (rep in 1:nreps) {
	
		dsps_data <- loadPopLog( path=path, rootname=rootname, rep=rep)
	
		# plot DSPS
		if (rep==1) {
			plot( dsps_data[,1], dsps_data[,2+d], type="l", col="orange", 
				ylim=c(0,N), xlim=c(0,400),
				xlab="Time", ylab="Number" )
		} else {
			lines(dsps_data[,1], dsps_data[,2+d], col="orange")
		}
		lines(dsps_data[,1], dsps_data[,3+d], col="red")
		lines(dsps_data[,1], dsps_data[,4+d], col="blue")
	
	}
	
	# plot ODE
	lines( ode_data[,1], ode_data[,2+d], col="black", lty=2, lwd=2)
	lines( ode_data[,1], ode_data[,3+d], col="black", lty=3, lwd=2)
	lines( ode_data[,1], ode_data[,4+d], col="black", lty=4, lwd=2)
	title(paste("DEME",dd))
	legend("left", c("DSPS S","DSPS I", "DSPS R", "ODE S","ODE I", "ODE R"), lty=c(1,1,1,2,3,4), bty="n",
				col=c("orange","red","blue","black","black","black"), cex=0.75 )
	legend("right", c( paste("b=",b), paste("y=",y), paste("N=",N) ), bty="n"   )
  }
	
	par(op)
	dev.off()
	
	
	# now load the trees and look at the from-to matrix
	mix01 <- matrix(0, nreps, 4)
	colnames(mix01) <- c("00","01","10","11")
	for (rep in 1:nreps) {
		tr 		<- loadTree(path=path,rootname=rootname,rep=rep)
		if (length(tr$tip.label) > 100) {
			mixTbl			<- table(tr$state[tr$edge[,1]],tr$state[tr$edge[,2]])
			mix01[rep,]		<- matrix( t(as.matrix(mixTbl)), 1, 4)
			#mix01[rep,]   	<- c(mixTbl[1,2],mixTbl[2,1])
		}
	}	
	inds <- which(apply(mix01, 1, sum) > 0)
	boxplot(mix01[inds,])

	rep <- 1
	imageName <- paste(path,rootname,"_rep",rep,"tree_image.png",sep="")
	png(file=imageName, height=3000, width=1800, res=300)
		tr 		<- loadTree(path=path,rootname=rootname,rep=rep)
		plotPropertyTree(ladderize(tr), cols=c("red","blue"), anc_pch=1, fill_anc=FALSE)
	dev.off()

}
	
###################################################################################
# infections over networks

doRandomNetwork <- FALSE
if (doRandomNetwork) {
	
	# change this to your path
	#path 	 <- "Documents//workspace//DiscreteSpatialPhyloSimulator//validation//"
	path		 <- "D://data//phylo_inference//DSPS_testing//"
	
	# change this to your name
	#rootname <- "RANDOM_NETWORK"
	rootname <- "RANDOM_1000"
	
	nreps	 <- 100
	for (rep in 1:nreps) {
		
		dsps_data 	<- loadPopLog( path=path, rootname=rootname, rep=rep)
		cn		<- colnames(dsps_data)
		tcol		<- which(cn=="Time")
		scol		<- which(cn=="S")
		icol		<- which(cn=="I")
		rcol		<- which(cn=="R")
		
		# plot DSPS
		if (rep==1) {
			
			#imageName <- paste(path,rootname,"_plots.png",sep="")
			#png(file=imageName, height=1800, width=1800, res=300)
			
			plot( dsps_data[,tcol], dsps_data[,icol], type="l", col="orange", 
				ylim=c(0,N), xlim=c(0,100),
				xlab="Time", ylab="Number" )
		} else {
			lines(dsps_data[,tcol], dsps_data[,icol], col="orange")
		}
		lines(dsps_data[,tcol], dsps_data[,scol], col="red")
		lines(dsps_data[,tcol], dsps_data[,rcol], col="blue")
	}
		
		fromTo		<- loadInfectionEventLog(path=path, rootname=rootname, rep=rep)
		tr		    <- loadTree(path=path, rootname=rootname, rep=rep)
		
		
		fromTo2		<- cbind(as.integer(tr$state[tr$edge[,1]]),as.integer(tr$state[tr$edge[,2]]))
		inds			<- which(!is.finite(fromTo2[,1]))
		fromTo2[inds,1] 	<- length(tr$tip.label)+1
		inds			<- which(fromTo2[,1]!=fromTo2[,2])
		fromTo2		<- fromTo2[inds,]+1

		library(statnet)
		N			<- 1000
		M			<- matrix(0, N, N)
		M[fromTo2] <- 1
		g			<- as.network(M)
		
		library(igraph)
		g			<- graph.adjlist(fromTo2+1)
		plot(g, layout=layout.kamada.kawai, vertex.size=1, vertex.label=NA)
		
	}
	
}	
	