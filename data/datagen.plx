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
my @relationships = ('parent', 'child', 'cousin', 'ward', 'sibling');
my @streets = ('Walnut', 'Cherry', 'Pine', 'Center', 'Birch', 'Cole', 'Winonine', 'Lamia');
my @abbrevs = ('St.', 'Rd.', 'Ln.', 'Blvd.', 'Pike', 'Ave.');
my @cities = ('Placeau', 'Runcham', 'Wantage', 'Canade', 'Bloomis', 'Lackawanna', 'Butzville', 'Bethlehem');

my @descriptions = ("Gosh it was awful.", "It was their fault", "I demand full reparations!", "It was like this, you see...", "They came at us so fast!", "I don''t remember it at all.");
my @titles = ("The _____ Incident", "Bash-Up on Mile 42", "The Idiosyncrasy Files", "Nose Funk No. 9", "Class of ''82 Brawler", "Theft of Heart", "Why Do We Give These Titles, Anyway?", "Broken Legs Are Catching, You Know", "This Feels Like a Silly Choice of Field", "You''ll Never Believe It", "Somehow I Feel These Repeat");

my %emprecs = (); my %agentrecs = (); my %adjrecs = ();
my @specialties = ('collision', 'valuables', 'property', 'machinery', 'wellness');
my @categories = ('jewelry', 'fine art', 'musical instruments', 'computer equipment');
my @vehicles = ('sedan', 'SUV', 'moped', 'motorcycle', 'mobile home', 'coupe');
for (my $i = 0; $i < 12; $i++) {
    $emprecs{&rand_id} = join(' ', shift(@fnames), shift(@lnames));
}
open(EMPLOYEES, '>', "output/employees.txt") or die $!;
open(AGENTS, '>', "output/agents.txt") or die $!;
open(ADJUSTERS, '>', "output/adjusters.txt") or die $!;
foreach my $key (keys %emprecs) {
    print EMPLOYEES "INSERT INTO employee VALUES ($key, '$emprecs{$key}');\n";
    if (int(rand(10)) > 5) {
        my $years_exp = int(rand(26));
        print AGENTS "INSERT INTO agent VALUES ($key, '$emprecs{$key}', $years_exp);\n";
        $agentrecs{$key} = $years_exp;
    }
    else {
        my $specialty = $specialties[rand(@specialties)];
        print ADJUSTERS "INSERT INTO adjuster VALUES ($key, '$emprecs{$key}', '$specialty');\n";
        $adjrecs{$key} = $specialty;
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
    $custrecs{$curr_id}{$agent} = (keys %agentrecs)[rand(keys %agentrecs)];
}
open(CUSTOMERS, '>', "output/customers.txt") or die $!;
open(DEPENDENTS, '>', "output/dependents.txt") or die $!;
open(ADDRESSES, '>', "output/addresses.txt") or die $!;
open(POLICIES, '>', "output/policies.txt") or die $!;
open(CLAIMS, '>', "output/claims.txt") or die $!;
open(ITEMS, '>', "output/items.txt") or die $!;
foreach my $key (keys %custrecs) {
    print CUSTOMERS "INSERT INTO customer VALUES ($key,
        '$custrecs{$key}{$fname}', '$custrecs{$key}{$minitial}', '$custrecs{$key}{$lname}',
        '$custrecs{$key}{$suffix}', DATE '$custrecs{$key}{$dob}', $custrecs{$key}{$agent});\n";
    my $address = int(rand(1000)) . " " . $streets[rand(@streets)] . " " . $abbrevs[rand(@abbrevs)];
    my $state = $initials[rand(@initials)] . $initials[rand(@initials)];
    my $city = $cities[rand(@cities)]; my $zipcode = int(rand(100000));
    print ADDRESSES "INSERT into cust_add VALUES ('$address', '$city', '$state', $zipcode, $key);\n";
    #if ($custrecs{$key}{$dob} < '1990-01-01') {
        my $dep_bounds = int(rand(3));
        for (my $i = 0; $i < $dep_bounds; $i++) {
            my $dname = shift(@fnames);
            my $relationship = $relationships[rand(@relationships)];
            my $dob = rand_date( min => '1920-01-01', max => '2020-01-01' );
            print DEPENDENTS "INSERT INTO dependentt VALUES ('$dname $custrecs{$key}{$lname}', '$relationship', DATE '$dob', $key);\n";
        }
    #}
    my $policy_bounds = int(rand(3));
    for (my $i = 0; $i < $policy_bounds; $i++) {
        my $policy_id = &rand_id;
        my $policy_type = $specialties[rand(@specialties)];
        my $quoted_price = int((rand() * 500) * 100) / 100;
        my $cancelled = 0; if (int(rand(10) > 8)) { $cancelled = 1; }
        print POLICIES "INSERT INTO polisy VALUES ($policy_id, '$policy_type', $quoted_price, $cancelled, $key);\n";
        my $item_bounds = int(rand(3));
        if ($policy_type eq "collision" || $policy_type eq "valuables") {
            for (my $i = 0; $i < $item_bounds; $i++) {
                my $item_id = &rand_id; my $item_type = "";
                if ($policy_type eq "valuables") { $item_type = $categories[rand(@categories)]; }
                if ($policy_type eq "collision") { $item_type = $vehicles[rand(@vehicles)]; }
                my $approx_value = int((rand() * 100000) * 100) / 100;
                print ITEMS "INSERT INTO item VALUES ($item_id, '$item_type', $approx_value, $policy_id);\n";
            }
        }
        if (int(rand(2)) % 2 == 0) {
            my $address = int(rand(1000)) . " " . $streets[rand(@streets)] . " " . $abbrevs[rand(@abbrevs)];
            my $state = $initials[rand(@initials)] . $initials[rand(@initials)];
            my $city = $cities[rand(@cities)]; my $zipcode = sprintf("%05d", int(rand(100000)));
            my $claim_loc = "$address, $city, $state, $zipcode";
            my $claim_id = &rand_id; my $description = $descriptions[rand(@descriptions)];
            my $date1 = rand_date( min => '2000-01-01', max => '2021-05-01' );
            my $date2 = int(rand(10)); my $title = $titles[rand(@titles)];
            print CLAIMS "INSERT INTO claim VALUES ($claim_id, '$title', '$claim_loc', '$description', DATE '$date1', DATE '$date1' + $date2, $policy_id);\n";
        }
    }
}

my @businesses = ("Great Value Ampersand Sons", "John Rice Catastrophics", "Everything You Hope You Never Need", "Urgent Care For...Something", "O''Reilly Associates");
open(CONTRACTORS, '>', "output/contractors.txt") or die $!;
open(FIRMADDS, '>', "output/firmadds.txt") or die $!;
for (my $i = 0; $i < 5; $i++) {
    my $curr_id = &rand_id;
    my $industry = $specialties[rand(@specialties)];
    my $firmnum = 5555550000 + ($i * 17);
    my $cname = shift(@businesses);
    print CONTRACTORS "INSERT INTO contractor VALUES ($curr_id, '$industry', '$cname', $firmnum);\n";

    my $address = int(rand(1000)) . " " . $streets[rand(@streets)] . " " . $abbrevs[rand(@abbrevs)];
    my $state = $initials[rand(@initials)] . $initials[rand(@initials)];
    my $city = $cities[rand(@cities)]; my $zipcode = int(rand(100000));
    print FIRMADDS "INSERT into firm_add VALUES ('$address', '$city', '$state', $zipcode, $curr_id);\n";
}
