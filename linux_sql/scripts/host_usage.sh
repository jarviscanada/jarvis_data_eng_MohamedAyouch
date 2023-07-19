#! /bin/sh
#Store CLI arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#Check # of args
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

#Store vmstat and hostname
vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

#Retrieve server usage data
memory_free=$(echo "$vmstat_mb" | awk '{print $4}'| tail -n1 | xargs)
cpu_idle=$(echo "$vmstat_mb" | awk '{print $15}' | tail -n1 | xargs )
cpu_kernel=$(echo "$vmstat_mb" | awk '{print $14}' | tail -n1 | xargs )
disk_io=$(echo "$vmstat_mb" | awk '{print $10}' | tail -n1 | xargs )
disk_available=$(echo "$(df -BM /)" | awk '{print $4}' | tr -d 'M' | tail -n1 | xargs)

#Get the date and time of the analysis
timestamp=$(echo "$(vmstat -t)" | awk ' {print $18, $19}' | tail -n1 | xargs)

#SQL query to find the correct host_id in the host_info table
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')";

#SQL command to insert a new row to host_usage table
insert_smt="INSERT INTO host_usage(timestamp,host_id,memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) VALUES('$timestamp', $host_id, $memory_free, $cpu_idle, $cpu_kernel,$disk_io, $disk_available)";

export PGPASSWORD=$psql_password

#Run the insert line
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_smt"
exit $?




