#!/usr/bin/env perl

use strict;
use warnings;

use List::Util qw/shuffle/;
use Data::Random qw(:all);

sub rand_id {
    my $numstring = "";
    for (my $i = 0; $i < 6; $i++) {
        $numstring .= int(rand(10));
    }
    return $numstring;
}

open(NAMES, '<', "names.txt") or die $!;
$/ = undef; # slurp to the end
my @lines = split('\n', <NAMES>);
my @fnames = shuffle(split(',', $lines[0]));
my @lnames = shuffle(split(',', $lines[1]));
my @initials = ('A'..'Z');
my @suffixes = ('Jr.', 'Sr.', 'Esq.', 'PhD.', 'I', 'II', 'III');

my %emprecs = ();
my @specialties = ('collision', 'valuables', 'property', 'machinery', 'wellness');
for (my $i = 0; $i < 12; $i++) {
    $emprecs{&rand_id} = join(' ', shift(@fnames), shift(@lnames));
}
open(EMPLOYEES, '>', "employees.txt") or die $!;
open(AGENTS, '>', "agents.txt") or die $!;
open(ADJUSTERS, '>', "adjusters.txt") or die $!;
foreach my $key (keys %emprecs) {
    print EMPLOYEES "INSERT INTO employee VALUES ($key, '$emprecs{$key}');\n";
    if (int(rand(10)) > 5) {
        my $years_exp = int(rand(26));
        print AGENTS "INSERT INTO agent VALUES ($key, '$emprecs{$key}', $years_exp);\n";
    }
    else {
        my $specialty = $specialties[rand(@specialties)];
        print ADJUSTERS "INSERT INTO adjuster VALUES ($key, '$emprecs{$key}', $specialty);\n";
    }
}

my %custrecs = ();
my ($fname, $lname, $minitial, $suffix, $dob, $agent) = ('fname', 'lname', 'minitial', 'suffix', 'dob', 'agent');
for (my $i = 0; $i < 60; $i++) {
    my $curr_id = &rand_id;
    $custrecs{$curr_id}{$fname} = shift(@fnames);
    $custrecs{$curr_id}{$lname} = shift(@lnames);
    if (int(rand(10)) > 4) { $custrecs{$curr_id}{$minitial} = $initials[rand(@initials)]; }
        else { $custrecs{$curr_id}{$minitial} = ""; }
    if (int(rand(10)) > 8) { $custrecs{$curr_id}{$suffix} = $suffixes[rand(@suffixes)]; }
        else { $custrecs{$curr_id}{$suffix} = ""; }
    $custrecs{$curr_id}{$dob} = rand_date( min => '1920-01-01', max => '1999-12-31' );
    $custrecs{$curr_id}{$agent} = (keys %emprecs)[rand(keys %emprecs)];
}
open(CUSTOMERS, '>', "customers.txt") or die $!;
foreach my $key (keys %custrecs) {
    print CUSTOMERS "INSERT INTO customer VALUES ($key,
        '$custrecs{$key}{$fname}', '$custrecs{$key}{$minitial}', '$custrecs{$key}{$lname}',
        '$custrecs{$key}{$suffix}', $custrecs{$key}{$dob}, $custrecs{$key}{$agent});\n";
}