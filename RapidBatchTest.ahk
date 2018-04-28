#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.

#MaxHotkeysPerInterval 100
SetKeyDelay [, 1, 100, -1]
$x::
Loop 
{
	if not GetKeyState("x", "P")
	break
	Send, {Tab}
	Sleep 10
	Send, {Space}
	Sleep 10
}
return