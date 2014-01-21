# quick SIR model test
# S. J. Lycett
# 6 March 09
# 12 March 09 - added vital dynamics (birth & death)

initialise  <- function( N ) {
	state 	= array(0,3)
	state[1] 	= (N-1)
	state[2]	= 1
	return ( state )
}

updateSIR	<- function( state, b, y, dt = 1 ) {
	# state[1] = S
	# state[2] = I
	# state[3] = R

	bSI = b*state[1]*state[2]
	yI  = y*state[2]
	dS = -bSI
	dI = bSI - yI
	dR = yI

	new_state	 = array(0, 3)
	new_state[1] = state[1] + dS*dt
	new_state[2] = state[2] + dI*dt
	new_state[3] = state[3] + dR*dt

	return ( new_state )
}

runSIR	<- function( N, b, y, maxReps = 10000, dt = 0.001) {
	states 	= matrix(0, 3, maxReps)
	states[,1]	= initialise(N)	
	
	i		= 1
	while ( (i < maxReps) & (states[1,i] >= 0) & (states[2,i] < N) & (states[3,i] < N) ) {
		states[,i+1] = updateSIR( states[,i], b, y, dt = dt )
		i = i+1
	}

	return ( states[,1:i] )

}

plotSIR	<- function( states, dt = 0.001, txt = "" ) {
	N = states[1,1]+states[2,1]+states[3,1]
	t = ( 1 : (length(states[1,])) ) * dt
	plot(t, states[1,], type = 'l', xlab = "Time", ylab = "Number", main = paste("SIR Model",txt),
		ylim=c(0,N), col='blue' )
	lines(t, states[2,], col = 'red')
	lines(t, states[3,], col = 'black')

	cstates = matrix(0, 4, length(t))
	cstates[1,] = t
	cstates[2:4,] = states
	return( cstates )
}

updateSIR_birthDeath	<- function( state, b, y, u, dt = 1 ) {

	N  	= sum(state)
	S	= state[1]
	I	= state[2]
	R	= state[3]

	bSI 	= b*S*I/N
	
	dS 	= u*N - u*S - bSI
	dI 	= bSI - (y+u)*I
	dR	= y*I - u*R

	new_state	 = array(0, 3)
	new_state[1] = state[1] + dS*dt
	new_state[2] = state[2] + dI*dt
	new_state[3] = state[3] + dR*dt

	return ( new_state )

}

runSIR_birthDeath	<- function( N, b, y, u, dt = 0.01, maxReps = 1000) {

	states 	= matrix(0, 3, maxReps)
	states[,1]	= initialise(N)	
	
	i		= 1
	while ( (i < maxReps) & (states[1,i] >= 0) & (states[2,i] < N) & (states[3,i] < N) ) {
		states[,i+1] = updateSIR_birthDeath( states[,i], b, y, u, dt = dt )
		i = i+1
	}

	return ( states[,1:i] )
}


updateSI_birthDeath	<- function( state, b, ub, ud, dt = 1) {

	S = state[1]
	I = state[2]

	bSI = b*S*I

	dS  = -bSI + ub
	dI  = bSI - ud

	new_state	 = array(0, 3)
	new_state[1] = state[1] + dS*dt
	new_state[2] = state[2] + dI*dt

	return ( new_state )

}

runSI_birthDeath	<- function( N, b, ub, ud, dt = 0.01, maxReps = 1000) {

	states 	= matrix(0, 3, maxReps)
	states[,1]	= initialise(N)	
	
	i		= 1
	while ( (i < maxReps) & (states[1,i] >= 0) & (states[2,i] < N) & (states[3,i] < N) ) {
		states[,i+1] = updateSI_birthDeath( states[,i], b, ub, ud, dt = dt )
		i = i+1
	}

	return ( states[,1:i] )

}


#################################################################
# example

exampleSIR	<- function(N = 100, b = 0.1, y = 0.5, 
			dt = 0.01, maxReps = 1000, fname = "x") {
	states  = runSIR(N, b, y, maxReps = maxReps, dt = dt)
	txt	  = paste("b=",b,"y=",y)
	cstates = plotSIR( states, dt = dt, txt = txt)

	if (fname != 'x') {
		write("Example SIR", file = fname, append=FALSE)
		write(paste("N=",N), file = fname, append=TRUE)
		write(paste("b=",b), file = fname, append=TRUE)
		write(paste("y=",y), file = fname, append=TRUE)
		write(paste("dt=",dt), file = fname, append=TRUE)
		write(paste("maxReps=",maxReps), file = fname, append=TRUE)
		write("T,S,I,R", file = fname, append=TRUE)
		write.table(t(cstates), file=fname, append=TRUE, col.names=FALSE, row.names=FALSE, sep = ",")
	}
}

exampleSIR_birthDeath	<- function(N = 500, b = 0.01, y = 0.001, u = 0.001, dt = 0.01, maxReps = 1000, fname = "x") {
	states  = runSIR_birthDeath(N, b, y, u, maxReps = maxReps, dt = dt)
	txt	  = paste("b=",b,"y=",y,"u=",u)
	cstates = plotSIR( states, dt = dt, txt = txt)

	if (fname != 'x') {
		write("Example SIR", file = fname, append=FALSE)
		write(paste("N=",N), file = fname, append=TRUE)
		write(paste("b=",b), file = fname, append=TRUE)
		write(paste("y=",y), file = fname, append=TRUE)
		write(paste("u=",u), file = fname, append=TRUE)
		write(paste("dt=",dt), file = fname, append=TRUE)
		write(paste("maxReps=",maxReps), file = fname, append=TRUE)
		write("T,S,I,R", file = fname, append=TRUE)
		write.table(t(cstates), file=fname, append=TRUE, col.names=FALSE, row.names=FALSE, sep = ",")
	}

}

exampleSI_birthDeath	<- function( N = 500, b = 0.01, ub = 0.001, ud = 0.001, dt = 0.01, maxReps = 1000, fname = "x") {
	states  = runSI_birthDeath(N, b, ub, ud, maxReps = maxReps, dt = dt)
	txt	  = paste("b=",b,"ub=",ub,"ud=",ud)
	cstates = plotSIR( states, dt = dt, txt = txt)

	if (fname != 'x') {
		write("Example SIR", file = fname, append=FALSE)
		write(paste("N=",N), file = fname, append=TRUE)
		write(paste("b=",b), file = fname, append=TRUE)
		write(paste("ub=",ub), file = fname, append=TRUE)
		write(paste("ud=",ud), file = fname, append=TRUE)
		write(paste("dt=",dt), file = fname, append=TRUE)
		write(paste("maxReps=",maxReps), file = fname, append=TRUE)
		write("T,S,I,R", file = fname, append=TRUE)
		write.table(t(cstates), file=fname, append=TRUE, col.names=FALSE, row.names=FALSE, sep = ",")
	}
}

# exampleSIR(N=1000,b=1/1000,y=0.5,maxReps=5000,dt=0.01,fname="C://Users//Samantha Lycett//workspace//DiscreteSpatialPhyloSimulator//validation//Rgenerated_SIR.csv")
# exampleSIR(N=1000,b=1/1000,y=0.0,maxReps=5000,dt=0.01,fname="C://Users//Samantha Lycett//workspace//DiscreteSpatialPhyloSimulator//validation//Rgenerated_SI.csv")

