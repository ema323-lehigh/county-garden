#!/usr/bin/env perl

use strict;
use warnings;

use List::Util qw/shuffle/;

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
my %records = ();
for (my $i = 0; $i < 12; $i++) {
    $records{&rand_id} = join(' ', shift(@fnames), shift(@lnames));
}

open(EMPLOYEES, '>', "employees.txt") or die $!;
foreach my $key (keys %records) {
    print EMPLOYEES "INSERT INTO employees VALUES ($key, '$records{$key}');\n";
}
